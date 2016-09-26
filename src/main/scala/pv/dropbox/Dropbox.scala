package pv
package dropbox

import com.twitter.finagle.http.{Fields, Request, RequestBuilder, Response}
import com.twitter.finagle.{Addr, Address, Http, Name, Service}
import com.twitter.io.Buf
import com.twitter.util.{Future, Var}
import failures.{InvalidCharsetFailure, JsonDecodingFailure}
import io.circe._
import io.circe.jawn.decode
import io.circe.syntax._
import java.net.URL
import models.{Entries, Path, Save}

object Dropbox {

  private val address = Address("api.dropbox.com", 443)
  private val client = Http.client
    .withTlsWithoutValidation
    .newService(
      Name.Bound(Var[Addr](Addr.Bound(address)), "api.dropbox.com"), "api.dropbox.com")

  case object SaveFile extends Service[Save, Int] {
    final def apply(save: Save): Future[Int] = {
      val request = buildRequest(save, "https://api.dropbox.com/2/files/save_url")
      for {
        response     <- client(request)
        responseCode =  response.getStatusCode
      } yield responseCode
    }
  }

  case object ListFolders extends Service[Path, Entries] {
    final def apply(path: Path): Future[Entries] = {
      val request = buildRequest(path, "https://api.dropbox.com/2/files/list_folder")
      client(request).flatMap(response => {
        val content = response.content match {
          case Buf.Utf8(s) => s
          case _           => ""
        }
        if(content.length == 0) Future.exception(new InvalidCharsetFailure())
        decode[Entries](content).fold(
          {l => Future.exception(new JsonDecodingFailure(l.toString))},
          {r => Future.value(r)}
        )
      })
    }
  }

  case object FolderExists extends Service[Path, Int] {
    final def apply(path: Path): Future[Int] = {
      val request = buildRequest(path, "https://api.dropbox.com/2/files/list_folder")
      for {
        response <- client(request)
        responseCode = response.getStatusCode
      } yield responseCode
    }
  }

  case object CreateFolder extends Service[Path, Int] {
    final def apply(path: Path): Future[Int] = {
      val request = buildRequest(path, "https://api.dropbox.com/2/files/create_folder")
      for {
        response     <- client(request)
        responseCode =  response.getStatusCode
      } yield responseCode
    }
  }

  private def buildRequest[A : Encoder](req: A, url: String): Request = 
    RequestBuilder()
      .url(new URL(url))
      .addHeader(Fields.UserAgent, "pv/0.0.1")
      .addHeader(Fields.ContentType, "application/json")
      .addHeader(Fields.Accept, "application/json")
      .addHeader(Fields.Authorization, s"Bearer ${Main.DropboxAccessToken()}")
      .buildPost(Buf.Utf8(req.asJson.noSpaces))

  private val log = Main.log
}
