package pv

object failures {
  final class VoiceMailNotImplemented extends Exception("Voice Mail payload not yet implemented")

  final class InvalidCharsetFailure extends Exception("Non-Utf8 Charset found in stream")
  final class JsonDecodingFailure(reason: String) extends Exception(reason)
  final class NoVotesFoundFailure(picture: String) extends Exception(
    s"No votes found for picture $picture"
  )
}
