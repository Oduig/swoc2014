package com.gjos.scala.swoc.protocol

import org.scalatest.{WordSpec, Matchers}
import scala.io.Source
import com.gjos.scala.swoc.util.{JsonConverters, Resource}
import com.gjos.scala.swoc.{Direction, Bot}

class BoardSpec extends WordSpec with Matchers {
  val us = Player.Black
  val (board, badMove) = {
    val lines = Source.fromFile(Resource.testResource("reinforce-weaker.txt")).getLines()
    val jsonBoard = lines.next()
    val badMove = JsonConverters.createProcessedMove(lines.next()).move
    val board = JsonConverters.createMoveRequest(jsonBoard).board
    (board, badMove)
  }

  "Board" should {
    "rate a situation correctly" in {
      board.score(us) should be (4f +- 0.9f)
      board.applyMove(badMove).score(us) should be (3f +- 0.9f)
      val goodMove = Move(MoveType.Attack, Some(BoardLocation fromLabel "E4"), Some(BoardLocation fromLabel "D3"))
      val boardAfter = board.applyMove(goodMove)
      boardAfter.score(us) should be (5f +- 0.9f)
    }

    "not make a move which results in a worse score" in {
      val before = board.score(us)
      val after = board.applyMove(badMove).score(us)
      before should be >= after
    }

    "say valid moves are valid" in {
      val emptyFromValid = board.isValidMove(BoardLocation(0, 0), BoardLocation(2, 0))
      val emptyToValid = board.isValidMove(BoardLocation(2, 0), BoardLocation(0, 0))
      val reinforceValid = board.isValidMove(BoardLocation(0, 3), BoardLocation(4, 3))
      val attackWeakerValid = board.isValidMove(BoardLocation(4, 3), BoardLocation(3, 2))
      val attackEqualValid = board.isValidMove(BoardLocation(2, 2), BoardLocation(3, 2))
      val attackStrongerValid = board.isValidMove(BoardLocation(2, 2), BoardLocation(2, 0))
      emptyFromValid should be (false)
      emptyToValid should be (false)
      reinforceValid should be (true)
      attackWeakerValid should be (true)
      attackEqualValid should be (true)
      attackStrongerValid should be (false)
    }

    "find first valid target in a direction" in {
      val firstInView = board.getFirstNonEmptyInDirection _
      val e4 = BoardLocation fromLabel "E4"
      val f4 = BoardLocation fromLabel "F4"
      val f1 = BoardLocation fromLabel "F1"
      val g4 = BoardLocation fromLabel "G4"
      val h2 = BoardLocation fromLabel "H2"
      firstInView(f4, Direction.North) should be (Some(f1))
      firstInView(f4, Direction.NorthEast) should be (Some(h2))
      firstInView(f4, Direction.SouthEast) should be (Some(g4))
      firstInView(f4, Direction.South) should be (None)
      firstInView(f4, Direction.SouthWest) should be (None)
      firstInView(f4, Direction.NorthWest) should be (Some(e4))
    }

    "get all possible to locations" in {
      val locations = board.getPossibleToLocations(BoardLocation fromLabel "G4").toSet
      locations should contain (BoardLocation fromLabel "H3")
      locations should contain (BoardLocation fromLabel "G7")
      locations should contain (BoardLocation fromLabel "E4")
      locations should not contain (BoardLocation fromLabel "D3")
      locations should not contain (BoardLocation fromLabel "I4")
    }
  }

}
