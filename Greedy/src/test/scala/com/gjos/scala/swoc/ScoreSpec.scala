package com.gjos.scala.swoc

import org.scalatest.{WordSpec, Matchers}
import scala.io.Source
import com.gjos.scala.swoc.util.{JsonConverters, Resource}
import com.gjos.scala.swoc.protocol.{Move, MoveType, Location, Player}

class ScoreSpec extends WordSpec with Matchers {
  val us = Player.Black
  def board(filename: String) = {
    val lines = Source.fromFile(Resource.testResource(filename)).getLines()
    val jsonBoard = lines.next()
    JsonConverters.createMoveRequest(jsonBoard).board
  }

  "Score" should {
    "rate a won board as +infinity" in {
      val goodMove = Move(MoveType.Attack, Location fromLabel "F2", Location fromLabel "I5")
      val after = board("win-or-lose.txt") applyMove goodMove
      after.score(us) should be (Int.MaxValue)
    }

    "rate a lost board as -infinity" in {
      val badMove = Move(MoveType.Attack, Location fromLabel "F2", Location fromLabel "F8")
      val after = board("win-or-lose.txt") applyMove badMove
      after.score(us) should be (Int.MinValue)
    }

    "rate another lost board as -infinity" in {
      val b = board("loss-in-2.txt")
      val badMove = Move(MoveType.Attack, Location fromLabel "I2", Location fromLabel "H3")
      val badMove2 = Move(MoveType.Attack, Location fromLabel "B6", Location fromLabel "H3")
      val after = b applyMove badMove applyMove badMove2
      after.score(us) should be (Int.MinValue)
    }
  }

}
