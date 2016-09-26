package pv
package dropbox

import io.circe.{Decoder, Encoder, Json}

object models {

  case class Save(path: String, url: String)
  object Save {
    implicit val encodeSave: Encoder[Save] = new Encoder[Save] {
      final def apply(save: Save): Json = Json.obj(
        ("path", Json.fromString(save.path)),
        ("url", Json.fromString(save.url))
      )
    }
  }

  case class Path(path: String)
  object Path {
    implicit val encodePath: Encoder[Path] = new Encoder[Path] {
      final def apply(path: Path): Json = Json.obj(
        ("path", Json.fromString(path.path))
      )
    }
  }

  case class Entry(name: String)
  object Entry {
    implicit val decodeEntry: Decoder[Entry] = Decoder.instance(c =>
      for {
        name <- c.downField("name").as[String]
      } yield Entry(name)
    )
  }

  case class Entries(entries: List[Entry])
  object Entries {
    implicit val decodeEntries: Decoder[Entries] = Decoder.instance(c =>
      for {
        entries <- c.downField("entries").as[List[Entry]]
      } yield Entries(entries)
    )
  }
}
