package pv

import io.circe._
import io.finch.circe._

object models {

  sealed abstract class BurnerWebhook(fromNumber: String, toNumber: String, userId: String,
    burnerId: String)
  object BurnerWebhook {
    implicit val decodeBurnerWebhook: Decoder[BurnerWebhook] = Decoder.instance(c => 
      c.downField("type").as[String].flatMap {
        case "inboundText"  => c.as[InboundText]
        case "inboundMedia" => c.as[InboundMedia]
        case "voiceMail"    => c.as[VoiceMail]
      }
    )
  }

  case class InboundText(textMessage: String, fromNumber: String, toNumber: String, userId: String,
    burnerId: String) extends BurnerWebhook(fromNumber, toNumber, userId, burnerId)
  object InboundText {
    implicit val decodeInboundText: Decoder[InboundText] = Decoder.instance(c =>
      for {
        payload    <- c.downField("payload").as[String]
        fromNumber <- c.downField("fromNumber").as[String]
        toNumber   <- c.downField("toNumber").as[String]
        userId     <- c.downField("userId").as[String]
        burnerId   <- c.downField("burnerId").as[String]
      } yield InboundText(textMessage = payload, fromNumber = fromNumber, toNumber = toNumber,
          userId = userId, burnerId = burnerId)
    )
  }

  case class InboundMedia(mediaUrl: String, fromNumber: String, toNumber: String, userId: String,
    burnerId: String) extends BurnerWebhook(fromNumber, toNumber, userId, burnerId)
  object InboundMedia {
    implicit val decodeInboundMedia: Decoder[InboundMedia] = Decoder.instance(c =>
      for {
        payload    <- c.downField("payload").as[String]
        fromNumber <- c.downField("fromNumber").as[String]
        toNumber   <- c.downField("toNumber").as[String]
        userId     <- c.downField("userId").as[String]
        burnerId   <- c.downField("burnerId").as[String]
      } yield InboundMedia(mediaUrl = payload, fromNumber = fromNumber, toNumber = toNumber,
          userId = userId, burnerId = burnerId)
    )
  }

  case class VoiceMail(mediaUrl: String, fromNumber: String, toNumber: String, userId: String,
    burnerId: String) extends BurnerWebhook(fromNumber, toNumber, userId, burnerId)
  object VoiceMail {
    implicit val decodeVoiceMail: Decoder[VoiceMail] = Decoder.instance(c =>
      for {
        payload    <- c.downField("payload").as[String]
        fromNumber <- c.downField("fromNumber").as[String]
        toNumber   <- c.downField("toNumber").as[String]
        userId     <- c.downField("userId").as[String]
        burnerId   <- c.downField("burnerId").as[String]
      } yield VoiceMail(mediaUrl = payload, fromNumber = fromNumber, toNumber = toNumber,
          userId = userId, burnerId = burnerId)
    )
  }

  case class Report(pictureName: String, numVotes: Long)
  object Report {
    implicit val encodeReport: Encoder[Report] = new Encoder[Report] {
      final def apply(report: Report): Json = Json.obj(
        (report.pictureName, Json.fromLong(report.numVotes))
      )
    }
  }
}
