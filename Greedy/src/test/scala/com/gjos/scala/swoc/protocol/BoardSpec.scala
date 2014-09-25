package com.gjos.scala.swoc.protocol

import org.scalatest.{WordSpec, Matchers}
import scala.io.Source
import com.gjos.scala.swoc.util.{JsonConverters, Resource}
import com.gjos.scala.swoc.Bot

class BoardSpec extends WordSpec with Matchers {
  val us = Player.Black
  def loadBoard(resource: String) = {
    val lines = Source.fromFile(Resource.testResource(resource)).getLines()
    val jsonBoard = lines.next()
    val board = JsonConverters.createMoveRequest(jsonBoard).board
    board
  }

  "Board" should {
    "rate a situation correctly" in {
      val board = loadBoard("reinforce-weaker.txt")
      val x0 = board.score(us)
      val goodMove = Move(MoveType.Attack, Location fromLabel "E4", Location fromLabel "D3")
      val badMove = Move(MoveType.Attack, Location fromLabel "A4", Location fromLabel "E4")
      val x1 = Board.applyMove(board, badMove).score(us)
      val x2 = Board.applyMove(board, goodMove).score(us)
      x1 should be < x0
      x2 should be > x0
    }
  }

}
