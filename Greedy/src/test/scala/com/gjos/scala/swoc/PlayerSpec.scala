package com.gjos.scala.swoc

import scala.io.Source
import com.gjos.scala.swoc.util.{JsonConverters, Resource}
import com.gjos.scala.swoc.protocol._
import org.scalatest.{WordSpec, Matchers}
import scala.Some
import com.gjos.scala.swoc.protocol.Move

class PlayerSpec extends WordSpec with Matchers {
  val board = {
    val lines = Source.fromFile(Resource.testResource("reinforce-weaker.txt")).getLines()
    val jsonBoard = lines.next()
    JsonConverters.createMoveRequest(jsonBoard).board
  }
  val us = Player.Black

  "Player" should {
    "get all valid moves in the starting situation" in {
      val b = new Board()
      val validMoves = us.allValidMoves(b).toSet
      val expectedAttacks = Map(
        "A5" -> "B6",
        "F8" -> "E9",
        "F8" -> "E8",
        "B5" -> "B6",
        "C4" -> "D4",
        "C4" -> "C3",
        "G2" -> "H2",
        "G2" -> "G3",
        "E1" -> "D1",
        "A2" -> "A1",
        "I5" -> "I4",
        "H5" -> "I4"
      )

      validMoves should contain (Move(MoveType.Pass, -1, -1))
      expectedAttacks foreach { case (src, dst) =>
        validMoves should contain (Move(MoveType.Attack, Location fromLabel src, Location fromLabel dst))
      }
    }

    "get valid moves in any situation" in {
      val validMove0 = Move(MoveType.Pass, -1, -1)
      val emptyFromValid = Move(MoveType.Attack, Location fromLabel "A1", Location fromLabel "C1")
      val emptyToValid = Move(MoveType.Strengthen, Location fromLabel "C1", Location fromLabel "A1")
      val reinforceValid = Move(MoveType.Strengthen, Location fromLabel "A4", Location fromLabel "E4")
      val attackWeakerValid = Move(MoveType.Attack, Location fromLabel "E4", Location fromLabel "D3")
      val attackEqualValid = Move(MoveType.Attack, Location fromLabel "C3", Location fromLabel "D3")
      val attackStrongerValid = Move(MoveType.Attack, Location fromLabel "C3", Location fromLabel "C1")
      val validMove1 = Move(MoveType.Attack, Location fromLabel "E4", Location fromLabel "D3")
      val validMove2 = Move(MoveType.Strengthen, Location fromLabel "A4", Location fromLabel "E4")
      val invalidMove = Move(MoveType.Strengthen, Location fromLabel "E4", Location fromLabel "E2")

      val moves = us.allValidMoves(board).toSet
      moves should contain (validMove0)
      moves should contain (validMove1)
      moves should contain (validMove2)
      moves should not contain emptyFromValid
      moves should not contain emptyToValid
      moves should contain (reinforceValid)
      moves should contain (attackWeakerValid)
      moves should contain (attackEqualValid)
      moves should not contain attackStrongerValid
      moves should not contain invalidMove
    }
  }
}
