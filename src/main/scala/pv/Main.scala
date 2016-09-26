package pv

import com.twitter.app.Flag
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import endpoints.{GetReport, IndexReports, PostBurnerCallback}
import io.finch._
import io.finch.circe._
import pv.dropbox.Dropbox
import pv.dropbox.models.Path

object Main extends TwitterServer {

  private val Api: Service[Request, Response] = (
    PostBurnerCallback :+: GetReport :+: IndexReports
  ).toService

  def main(): Unit = {
    ensureFolderExistence()

    log.info("Serving Picture Voting Application")
    val server = Http.server
      .withStatsReceiver(statsReceiver)
      .serve(s":${port()}", Api)

    onExit { server.close(); () }
    Await.ready(adminHttpServer)
    ()
  }

  private def ensureFolderExistence(): Unit = {
    val path = Path(DropboxFolder())
    log.info(s"Ensuring Folder existence for ${DropboxFolder()}")
    val code = Await.result(Dropbox.FolderExists(path))
    if(code == 200) { // we're all good, folder exists
      log.info(s"${DropboxFolder()} exists, proceeding through server startup")
      return ()
    } else { // create folder
      val createdResponse = Await.result(Dropbox.CreateFolder(path))
      log.info(s"${DropboxFolder()} DNE, attempting to create")
      if(createdResponse == 200) {
        log.info(s"${DropboxFolder()} created, proceeding through server startup")
        ()
      } else {
        log.fatal(s"${DropboxFolder()} could not be created, Dropbox returned $createdResponse. Shutting down.")
        System.exit(1)
      }
    }
  }

  val DropboxAccessToken: Flag[String] = flag("dropbox.accessToken", "", 
    "Access Token for the Dropbox Api")
  val DropboxFolder: Flag[String] = flag("dropbox.folder", "/", "Dropbox Folder")
  private val port: Flag[Int] = flag("port", 8081, "TCP port for HTTP server")
}
