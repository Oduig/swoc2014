package com.gjos.scala.swoc.protocol

import org.scalatest.{WordSpec, Matchers}
import scala.io.Source
import com.gjos.scala.swoc.util.{JsonConverters, Resource}

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
      board.utility(us) should be (4.0)
      board.applyMove(badMove).utility(us) should be (3.0)
      val goodMove = Move(MoveType.Attack, Some(BoardLocation fromLabel "E4"), Some(BoardLocation fromLabel "D3"))
      val boardAfter = board.applyMove(goodMove)
      boardAfter.utility(us) should be (5.0)
    }

    "not make a move which results in a worse score" in {
      val before = board.utility(us)
      val after = board.applyMove(badMove).utility(us)
      before should be >= after
    }
  }

}
