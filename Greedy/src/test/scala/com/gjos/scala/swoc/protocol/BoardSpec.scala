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
      val x0 = board.score(us)
      val x1 = board.applyMove(badMove).score(us)
      val goodMove = Move(MoveType.Attack, Some(BoardLocation fromLabel "E4"), Some(BoardLocation fromLabel "D3"))
      val boardAfter = board.applyMove(goodMove)
      val x2 = boardAfter.score(us)
      x1 should be < x0
      x2 should be > x0
    }
  }

}
