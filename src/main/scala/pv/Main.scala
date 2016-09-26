package pv

import com.twitter.app.Flag
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.finch._
import io.finch.circe._
import endpoints.{GetReport, PostBurnerCallback}

object Main extends TwitterServer {

  val port: Flag[Int] = flag("port", 8081, "TCP port for HTTP server")
  private val Api: Service[Request, Response] = (PostBurnerCallback :+: GetReport).toService

  def main(): Unit = {
    log.info("Serving Picture Voting Application")
    val server = Http.server
      .withStatsReceiver(statsReceiver)
      .serve(s":${port()}", Api)

    onExit { server.close(); () }
    Await.ready(adminHttpServer)
    ()
  }
}
