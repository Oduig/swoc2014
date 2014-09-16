package com.gjos.scala.swoc

import scala.io.Source
import com.gjos.scala.swoc.util.{JsonConverters, Resource}
import com.gjos.scala.swoc.protocol.{MoveType, Move, BoardLocation, Player}
import org.scalatest.{WordSpec, Matchers}

class PlayerSpec extends WordSpec with Matchers {
  val board = {
    val lines = Source.fromFile(Resource.testResource("reinforce-weaker.txt")).getLines()
    val jsonBoard = lines.next()
    JsonConverters.createMoveRequest(jsonBoard).board
  }
  val us = Player.Black

  "Player" should {
    "get all valid moves" in {
      val moves = us.allValidMoves(board).toSet
      val e4 = BoardLocation fromLabel "E4"
      val validMove1 = Move(MoveType.Attack, Some(e4), Some(BoardLocation fromLabel "D3"))
      val validMove2 = Move(MoveType.Strengthen, Some(BoardLocation fromLabel "A4"), Some(e4))
      val invalidMove = Move(MoveType.Strengthen, Some(e4), Some(BoardLocation fromLabel "E2"))
      moves should contain(validMove1)
      moves should contain(validMove2)
      moves should not contain invalidMove
    }
  }
}
