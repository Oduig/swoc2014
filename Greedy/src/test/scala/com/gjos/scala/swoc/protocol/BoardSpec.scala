package com.gjos.scala.swoc.protocol

import org.scalatest.{WordSpec, Matchers}
import scala.io.Source
import com.gjos.scala.swoc.util.{JsonConverters, Resource}
import com.gjos.scala.swoc.{Direction, Bot}

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
      val goodMove = Move(MoveType.Attack, BoardLocation fromLabel "E4", BoardLocation fromLabel "D3")
      val badMove = Move(MoveType.Attack, (0, 3), (4, 3))
      val x1 = board.applyMove(badMove).score(us)
      val x2 = board.applyMove(goodMove).score(us)
      x1 should be < x0
      x2 should be > x0
    }
  }

}
