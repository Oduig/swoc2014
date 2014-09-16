package com.gjos.scala.swoc

import org.scalatest.{WordSpec, Matchers}
import scala.io.Source
import com.gjos.scala.swoc.util.{JsonConverters, Resource}
import com.gjos.scala.swoc.protocol.{MoveType, Move, BoardLocation, Player}

class BotSpec extends WordSpec with Matchers {
  def moveRequest(fileName: String) = {
    val lines = Source.fromFile(Resource.testResource(fileName)).getLines()
    val jsonBoard = lines.next()
    JsonConverters.createMoveRequest(jsonBoard)
  }
  val us = Player.Black

  "Bot" should {
    "should not miss a win-in-2" in {
      val move = new Bot(Some(us)).handleMove(moveRequest("win-in-2.txt"), false)
      println(move)
      move should be (Move(MoveType.Attack, Some(BoardLocation fromLabel "I2"), Some(BoardLocation fromLabel "H3")))
    }

    "should not miss a loss-in-2" in {
      val move = new Bot(Some(us)).handleMove(moveRequest("loss-in-2.txt"), false)
      println(move)
      move should not be Move(MoveType.Attack, Some(BoardLocation fromLabel "I2"), Some(BoardLocation fromLabel "H3"))
    }
  }
}
