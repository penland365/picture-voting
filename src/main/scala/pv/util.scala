package pv

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import models.Report

object util {

  def nextInt: Int = synchronized {
    val next = counter + 1
    counter = next
    return counter - 1
  }

  def addVote(picture: String): Unit = synchronized {
    voteMap.get(picture) match {
      case Some(currentVotes) => {
        voteMap.put(picture, currentVotes + 1)
      }
      case None => voteMap.put(picture, 1)
    }
    ()
  }

  def allVotes: List[Report] = voteMap.toList.map(x => Report(x._1, x._2))
  def votesForPicture(picture: String): Option[Report] = 
    voteMap.get(picture).map(votes => Report(picture, votes))

  def buildFullPath(url: String): String = {
    val fileExtension = url.split("\\.").last
    val fileName = s"IMG_" + util.nextInt + "." + fileExtension
    s"${Main.DropboxFolder()}/$fileName"
  }

  private[this] var counter: Int = 0;
  private[this] val voteMap = new ConcurrentHashMap[String, Int]().asScala

  private[pv] def resetVotes(): Unit = voteMap.clear() // exposed for testing
}
