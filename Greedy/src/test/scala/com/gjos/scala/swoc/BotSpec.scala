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
  val samples = 10

  "Bot" should {
    "should never miss a win-in-2" in {
      Iterator.fill(samples)(()) foreach { _ =>
        val move = new Bot(Some(us)).handleMove(moveRequest("win-in-2.txt"), singleMoveTurn = false)
        move should be (Move(MoveType.Attack, BoardLocation fromLabel "I2", BoardLocation fromLabel "H3"))
      }
    }

    "should never miss a loss-in-2" in {
      Iterator.fill(samples)(()) foreach { _ =>
        val mr = moveRequest("loss-in-2.txt")
        val move = new Bot(Some(us)).handleMove(mr, singleMoveTurn = false)
        val wrong = Move(MoveType.Attack, BoardLocation fromLabel "I2", BoardLocation fromLabel "H3")
        if (move == wrong) (mr.board applyMove move).dump()
        move should not be wrong
      }
    }

    "should not suicide even when only losing moves are available" in {
      Iterator.fill(samples)(()) foreach { _ =>
        val move = new Bot(Some(us)).handleMove(moveRequest("suicide.txt"), singleMoveTurn = false)
        move should not be Move(MoveType.Strengthen, BoardLocation fromLabel "G1", BoardLocation fromLabel "I1")
      }
    }

    "should not make a move that allows the opponent to prevent us from moving the next turn" in {
      Iterator.fill(samples)(()) foreach { _ =>
        val move = new Bot(Some(us)).handleMove(moveRequest("loss-by-lack-of-move.txt"), singleMoveTurn = false)
        move should not be Move(MoveType.Attack, BoardLocation fromLabel "H1", BoardLocation fromLabel "E1")
      }
    }

    "should not do things that are fucking stupid" in {
      val move = new Bot(Some(us)).handleMove(moveRequest("fucking-stupid.txt"), singleMoveTurn = false)
      move.moveType should not be MoveType.Attack
    }
  }
}
