package com.gjos.scala.swoc

import org.scalatest.{WordSpec, Matchers}
import scala.io.Source
import com.gjos.scala.swoc.util.{JsonConverters, Resource}
import com.gjos.scala.swoc.protocol.{MoveType, Move, BoardLocation, Player}

class BotSpec extends WordSpec with Matchers {
  val board = {
    val lines = Source.fromFile(Resource.testResource("reinforce-weaker.txt")).getLines()
    val jsonBoard = lines.next()
    JsonConverters.createMoveRequest(jsonBoard).board
  }
  val us = Player.Black

  "Bot" should {
    "say valid moves are valid" in {
      val emptyFromValid = Bot.isValidMove(board, BoardLocation(0, 0), BoardLocation(2, 0))
      val emptyToValid = Bot.isValidMove(board, BoardLocation(2, 0), BoardLocation(0, 0))
      val reinforceValid = Bot.isValidMove(board, BoardLocation(0, 3), BoardLocation(4, 3))
      val attackWeakerValid = Bot.isValidMove(board, BoardLocation(4, 3), BoardLocation(3, 2))
      val attackEqualValid = Bot.isValidMove(board, BoardLocation(2, 2), BoardLocation(3, 2))
      val attackStrongerValid = Bot.isValidMove(board, BoardLocation(2, 2), BoardLocation(2, 0))
      emptyFromValid should be (false)
      emptyToValid should be (false)
      reinforceValid should be (true)
      attackWeakerValid should be (true)
      attackEqualValid should be (true)
      attackStrongerValid should be (false)
    }

    "find first valid target in a direction" in {
      val firstInView = Bot.getFirstNonEmptyInDirection _
      val e4 = BoardLocation fromLabel "E4"
      val f4 = BoardLocation fromLabel "F4"
      val f1 = BoardLocation fromLabel "F1"
      val g4 = BoardLocation fromLabel "G4"
      val h2 = BoardLocation fromLabel "H2"
      firstInView(board, f4, Direction.North) should be (Some(f1))
      firstInView(board, f4, Direction.NorthEast) should be (Some(h2))
      firstInView(board, f4, Direction.SouthEast) should be (Some(g4))
      firstInView(board, f4, Direction.South) should be (None)
      firstInView(board, f4, Direction.SouthWest) should be (None)
      firstInView(board, f4, Direction.NorthWest) should be (Some(e4))
    }

    "get all possible to locations" in {
      val locations = Bot.getPossibleToLocations(board, BoardLocation fromLabel "G4")
      locations should contain (BoardLocation fromLabel "H3")
      locations should contain (BoardLocation fromLabel "G7")
      locations should contain (BoardLocation fromLabel "E4")
      locations should not contain (BoardLocation fromLabel "D3")
      locations should not contain (BoardLocation fromLabel "I4")
    }

    "get all valid moves" in {
      val moves = Bot.allValidMoves(board, Some(us))
      val e4 = BoardLocation fromLabel "E4"
      val validMove1 = Move(MoveType.Attack, Some(e4), Some(BoardLocation fromLabel "D3"))
      val validMove2 = Move(MoveType.Strengthen, Some(BoardLocation fromLabel "A4"), Some(e4))
      val invalidMove = Move(MoveType.Strengthen, Some(e4), Some(BoardLocation fromLabel "E2"))
      moves should contain (validMove1)
      moves should contain (validMove2)
      moves should not contain invalidMove
    }
  }
}
