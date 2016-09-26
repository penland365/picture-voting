package pv

import io.finch._
import io.finch.circe._
import failures.{NoVotesFoundFailure, VoiceMailNotImplemented}
import models._
import dropbox.Dropbox
import dropbox.models.{Path, Save}
import com.twitter.util.Future
import util._

object endpoints {

  def PostBurnerCallback: Endpoint[Unit] = post("event" :: postedWebhook) { (ps: BurnerWebhook) =>
    ps match {
      case x:InboundText  => handleInboundText(x)
      case x:InboundMedia => handleInboundMedia(x)
      case x:VoiceMail    => Future.value(NotImplemented(new VoiceMailNotImplemented()))
    }
  }

  private def handleInboundText(inbound: InboundText): Future[Output[Unit]] = {
    val path = Path(Main.DropboxFolder())
    Dropbox.ListFolders(path).map(entries => {
      val picture = entries.entries.find(_.name.equalsIgnoreCase(inbound.textMessage))
      picture match {
        case Some(x) => addVote(x.name)
        case None    => ()
      }
      Created(())
    })
  }
  
  private def handleInboundMedia(inbound: InboundMedia): Future[Output[Unit]] = {
    val fullPath = buildFullPath(inbound.mediaUrl)
    val save = Save(fullPath, inbound.mediaUrl)
    log.info(s"Saving $save to Dropbox")
    for {
      status <- Dropbox.SaveFile(save)
      response = status match {
        case 200 => Created(())
        case x   => InternalServerError(new Exception(x.toString))
      } 
    } yield response
  }

  def GetReport: Endpoint[Report] = get("report" :: string("picture")) { picture: String =>
    log.info(s"Searching for all votes for picture $picture")
    votesForPicture(picture) match {
      case Some(report) => Ok(report)
      case None         => NotFound(new NoVotesFoundFailure(picture))
    }
  }

  def IndexReports: Endpoint[List[Report]] = get("reports") {
    val reports = allVotes
    Ok(reports)
    
  }
  private def postedWebhook = body.as[BurnerWebhook]

  private lazy val log = Main.log
}
