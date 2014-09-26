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

      val moves = Player.allValidMoves(us, board)
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
