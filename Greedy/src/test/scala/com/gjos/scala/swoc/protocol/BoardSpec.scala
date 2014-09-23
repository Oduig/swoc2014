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
      board.score(us) should be (218)
      board.applyMove(badMove).score(us) should be (147)
      val goodMove = Move(MoveType.Attack, Some(BoardLocation fromLabel "E4"), Some(BoardLocation fromLabel "D3"))
      val boardAfter = board.applyMove(goodMove)
      boardAfter.score(us) should be (355)
    }

    "not make a move which results in a worse score" in {
      val before = board.score(us)
      val after = board.applyMove(badMove).score(us)
      before should be >= after
    }
  }

}
