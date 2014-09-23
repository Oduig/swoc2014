package com.gjos.scala.swoc

import org.scalatest.{WordSpec, Matchers}
import scala.io.Source
import com.gjos.scala.swoc.util.{JsonConverters, Resource}
import com.gjos.scala.swoc.protocol.{Move, MoveType, BoardLocation, Player}

class ScoreSpec extends WordSpec with Matchers {
  val us = Player.Black
  def board(filename: String) = {
    val lines = Source.fromFile(Resource.testResource(filename)).getLines()
    val jsonBoard = lines.next()
    JsonConverters.createMoveRequest(jsonBoard).board
  }

  "Score" should {
    "rate a won board as +infinity" in {
      val goodMove = Move(MoveType.Attack, Some((5.toByte, 8.toByte)), Some((8.toByte, 8.toByte)))
      val after = board("win-or-lose.txt") applyMove goodMove
      after.score(us) should be (Int.MaxValue)
    }

    "rate a lost board as -infinity" in {
      val badMove = Move(MoveType.Attack, Some((5.toByte, 2.toByte)), Some((5.toByte, 8.toByte)))
      val after = board("win-or-lose.txt") applyMove badMove
      after.score(us) should be (Int.MinValue)
    }

    "rate another lost board as -infinity" in {
      val b = board("loss-in-2.txt")
      val badMove = Move(MoveType.Attack, Some(BoardLocation fromLabel "I2"), Some(BoardLocation fromLabel "H3"))
      val badMove2 = Move(MoveType.Attack, Some(BoardLocation fromLabel "B6"), Some(BoardLocation fromLabel "H3"))
      val after = b applyMove badMove applyMove badMove2
      after.score(us) should be (Int.MinValue)
    }
  }

}
