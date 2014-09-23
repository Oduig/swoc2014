package com.gjos.scala.swoc.protocol

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import com.gjos.scala.swoc.Direction

trait Player {
  val value: Int
  val opponent: Player

  def allValidMoves(board: Board, attackOnly: Boolean = false): Vector[Move] = {
    var v = if (attackOnly) Vector.empty[Move] else Vector(Move(MoveType.Pass, null, null))

    def discover(startX: Int, startY: Int, direction: Direction) {
      var x = startX
      var y = startY
      var prevX = -1
      var prevY = -1
      while (x < 9 && y < 9) {
        if ((x == 4 && y == 4) || (x - y >= 5 && y - x >= 5)) {
          prevX = -1
          prevY = -1
        } else {
          val cur = board.getField(x, y)
          if (cur != 0) {
            if (prevX >= 0) {
              val prev = board.getField(prevX, prevY)
              val prevPlayer = Field.player(prev)
              val curPlayer = Field.player(cur)
              val prevHeight = Field.height(prev)
              val curHeight = Field.height(cur)
              if (prevPlayer == this && curPlayer == this && !attackOnly) {
                v = v :+ Move(MoveType.Strengthen, (x, y), (prevX, prevY))
                v = v :+ Move(MoveType.Strengthen, (prevX, prevY), (x, y))
              } else if (prevPlayer != this && curPlayer == this && curHeight >= prevHeight) {
                v = v :+ Move(MoveType.Attack, (x, y), (prevX, prevY))
              } else if (prevPlayer == this && curPlayer != this && prevHeight >= curHeight) {
                v = v :+ Move(MoveType.Attack, (prevX, prevY), (x, y))
              }
            }
            prevX = x
            prevY = y
          }
        }
        x = x + direction.x
        y = y + direction.y
      }
    }

    for (y <- Board.fullRange) discover(0, y, Direction.NorthEast)
    for (x <- Board.fullRange) discover(x, 0, Direction.South)
    for (y <- Board.halfRange) discover(0, y, Direction.SouthEast)
    for (x <- Board.halfRange if x > 0) discover(x, 0, Direction.SouthEast)
    v
  }


}

object Player {
  case object White extends Player {
    val value: Int = 1
    val opponent = Player.Black
  }

  case object Black extends Player {
    val value: Int = -1
    val opponent = Player.White
  }

  def byValue(i: Int): Option[Player] = i match {
    case _ if i > 0 => Some(White)
    case _ if i < 0 => Some(Black)
    case _ => None
  }
}
