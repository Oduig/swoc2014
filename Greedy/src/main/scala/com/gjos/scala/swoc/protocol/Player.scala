package com.gjos.scala.swoc.protocol

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import com.gjos.scala.swoc.Direction

trait Player {
  val value: Byte
  val opponent: Player

  def allValidMoves(board: Board, attackOnly: Boolean = false): Vector[Move] = {
    var v = Vector(Move(MoveType.Pass, None, None))

    def discover(startX: Byte, startY: Byte, direction: Direction) {
      var x = startX
      var y = startY
      var prevX: Byte = -1
      var prevY: Byte = -1
      while (x < 9 && y < 9) {
        if ((x == 4 && y == 4) || (x - y >= 5 && y - x >= 5)) {
          prevX = -1
          prevY = -1
        } else {
          val cur = board.getField(x, y)
          if (cur != 0) {
            if (prevX >= 0) {
              val prev = board.getField(prevX, prevY)
              val prevPlayer = Field.player(prev).get
              val curPlayer = Field.player(cur).get
              val prevHeight = Field.height(prev)
              val curHeight = Field.height(cur)
              if (prevPlayer == this && curPlayer == this && !attackOnly) {
                v = v :+ Move(MoveType.Strengthen, Some((x, y)), Some((prevX, prevY)))
                v = v :+ Move(MoveType.Strengthen, Some((prevX, prevY)), Some((x, y)))
              } else if (prevPlayer != this && curPlayer == this && curHeight >= prevHeight) {
                v = v :+ Move(MoveType.Attack, Some((x, y)), Some((prevX, prevY)))
              } else if (prevPlayer == this && curPlayer != this && prevHeight >= curHeight) {
                v = v :+ Move(MoveType.Attack, Some((prevX, prevY)), Some((x, y)))
              }
            }
            prevX = x
            prevY = y
          }
        }
        x = (x + direction.x).toByte
        y = (y + direction.y).toByte
      }
    }

    for (y <- Board.fullRange) discover(0, y, Direction.NorthEast)
    for (x <- Board.fullRange) discover(x, 0, Direction.South)
    for (y <- Board.halfRange) discover(0, y, Direction.SouthEast)
    for (x <- Board.halfRange) discover(x, 0, Direction.SouthEast)
    v
  }


}

object Player {
  case object White extends Player {
    val value: Byte = 1
    val opponent = Player.Black
  }

  case object Black extends Player {
    val value: Byte = -1
    val opponent = Player.White
  }

  def byValue(i: Byte): Option[Player] = i match {
    case _ if i > 0 => Some(White)
    case _ if i < 0 => Some(Black)
    case _ => None
  }
}
