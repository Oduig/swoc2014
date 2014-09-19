package com.gjos.scala.swoc.protocol

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import com.gjos.scala.swoc.Direction

trait Player {
  val value: Int
  val opponent: Player

  def allValidMoves(board: Board, attackOnly: Boolean = false): Vector[Move] = {
    var v = Vector(Move(MoveType.Pass, None, None))

    def discover(startX: Int, startY: Int, direction: Direction) {
      var x = startX
      var y = startY
      var prevX: Int = -1
      var prevY: Int = -1
      while (x < 9 && y < 9) {
        if ((x == 4 && y == 4) || (x - y >= 5 && y - x >= 5)) {
          prevX = -1
          prevY = -1
        } else {
          val cur = board.getField(x, y)
          if (cur.player.nonEmpty) {
            if (prevX >= 0) {
              val prev = board.getField(prevX, prevY)
              if (prev.player.get == this && cur.player.get == this && !attackOnly) {
                v = v :+ Move(MoveType.Strengthen, Some((x, y)), Some((prevX, prevY)))
                v = v :+ Move(MoveType.Strengthen, Some((prevX, prevY)), Some((x, y)))
              } else if (prev.player.get != this && cur.player.get == this && cur.height >= prev.height) {
                v = v :+ Move(MoveType.Attack, Some((x, y)), Some((prevX, prevY)))
              } else if (prev.player.get == this && cur.player.get != this && prev.height >= cur.height) {
                v = v :+ Move(MoveType.Attack, Some((prevX, prevY)), Some((x, y)))
              }
            }
            prevX = x
            prevY = y
          }
        }
        x += direction.x
        y += direction.y
      }
    }

    for (y <- 0 until 9) discover(0, y, Direction.NorthEast)
    for (x <- 0 until 9) discover(x, 0, Direction.South)
    for (y <- 0 until 5) discover(0, y, Direction.SouthEast)
    for (x <- 0 until 5) discover(x, 0, Direction.SouthEast)
    v
  }
}

object Player {
  case object White extends Player {
    val value = 1
    val opponent = Player.Black
  }

  case object Black extends Player {
    val value = -1
    val opponent = Player.White
  }

  def byValue(i: Int): Option[Player] = i match {
    case _ if i > 0 => Some(White)
    case _ if i < 0 => Some(Black)
    case _ => None
  }
}
