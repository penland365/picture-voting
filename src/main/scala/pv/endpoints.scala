package pv

import io.finch._
import io.finch.circe._
import models._

object endpoints {

  def PostBurnerCallback: Endpoint[Unit] = post("event" :: postedWebhook) { (ps: BurnerWebhook) =>
    Created(())
  }

  def GetReport: Endpoint[Report] = get("report") {
    val report = Report("my_great_pic.jpg", 71)
    Ok(report)
  }
  private def postedWebhook = body.as[BurnerWebhook]
}
