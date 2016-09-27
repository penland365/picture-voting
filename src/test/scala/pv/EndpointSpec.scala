package pv

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Await, Duration}
import org.scalatest.{fixture, Outcome}
import com.twitter.finagle.http.Status
import com.twitter.io.{Buf, Charsets}
import io.finch._
import io.finch.circe._
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck._
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.Checkers

final class EndpointSpec extends FlatSpec with Matchers with Checkers {
  import endpoints._
  import models._

  behavior of "the report/IMAGE_NAME endpoint"
    it should "return a 404 if the image has no votes" in {
      util.resetVotes()
      val input = Input.get(s"/report/rando")
      val result = GetReport(input)
      result.output.map(_.status) shouldBe Some(Status.NotFound)
    }
    it should "return a 200 and Report if the image has votes" in {
      util.resetVotes()
      val picture = "IMG_0.jpg"
      util.addVote(picture)
      val input = Input.get(s"/report/$picture")
      val result = GetReport(input)
      result.output.map(_.status) shouldBe Some(Status.Ok)
      result.value.map(_.numVotes) shouldBe Some(1L)
    }

  behavior of "the reports endpoint"
    it should "return an empty array if no votes have been cast" in {
      util.resetVotes()
      val input = Input.get("/reports")
      val result = IndexReports(input)
      result.output.map(_.status) shouldBe Some(Status.Ok)
      result.value shouldBe Some(List.empty[Report])
    }
    it should "return an array containing reports for each image with votes" in {
      util.resetVotes()
      val picture0 = "IMG_0.jpg"
      val picture1 = "IMG_1.jpg"
      util.addVote(picture0)
      util.addVote(picture0)
      util.addVote(picture1)
      
      val input = Input.get("/reports")
      val result = IndexReports(input)
      result.output.map(_.status) shouldBe Some(Status.Ok)
      result.value.map(_.length) shouldBe Some(2)
      result.value.map(_.head.numVotes) shouldBe Some(1)
      result.value.map(_.tail.head.numVotes) shouldBe Some(2)
    }

}
