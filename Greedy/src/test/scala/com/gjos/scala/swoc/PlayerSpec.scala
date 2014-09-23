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

      validMoves should contain (Move(MoveType.Pass, null, null))
      expectedAttacks foreach { case (src, dst) =>
        validMoves should contain (Move(MoveType.Attack, BoardLocation fromLabel src, BoardLocation fromLabel dst))
      }
    }

    "get valid moves in any situation" in {
      val e4 = BoardLocation fromLabel "E4"
      val validMove0 = Move(MoveType.Pass, null, null)
      val emptyFromValid = Move(MoveType.Attack, (0, 0), (2, 0))
      val emptyToValid = Move(MoveType.Strengthen, (2, 0), (0, 0))
      val reinforceValid = Move(MoveType.Strengthen, (0, 3), (4, 3))
      val attackWeakerValid = Move(MoveType.Attack, (4, 3), (3, 2))
      val attackEqualValid = Move(MoveType.Attack, (2, 2), (3, 2))
      val attackStrongerValid = Move(MoveType.Attack, (2, 2), (2, 0))
      val validMove1 = Move(MoveType.Attack, e4, BoardLocation fromLabel "D3")
      val validMove2 = Move(MoveType.Strengthen, BoardLocation fromLabel "A4", e4)
      val invalidMove = Move(MoveType.Strengthen, e4, BoardLocation fromLabel "E2")

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
