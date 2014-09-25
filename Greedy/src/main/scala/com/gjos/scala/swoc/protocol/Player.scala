package com.gjos.scala.swoc.protocol

import java.util

trait Player {
  val value: Int
  val opponent: Player

  def allValidMoves(board: FastBoard, attackOnly: Boolean = false): util.ArrayList[Move] = {
    if (MoveCache.hasKey(board, attackOnly)) {
      MoveCache.get(board, attackOnly)
    } else {
      val v = new util.ArrayList[Move]()
      if (!attackOnly) v.add(Move(MoveType.Pass, -1, -1))

      def discover(start: Location, direction: Direction) {
        var curLoc: Location = start
        var prevLoc: Location = -1
        while (curLoc < 9 * 9) {
          if (!FastLocation.isValid(curLoc)) {
            prevLoc = -1
          } else {
            val cur = board.getField(curLoc)
            if (cur != 0) {
              if (prevLoc >= 0) {
                val prev = board.getField(prevLoc)
                val prevPlayer = Field.player(prev)
                val curPlayer = Field.player(cur)
                val prevHeight = Field.height(prev)
                val curHeight = Field.height(cur)
                if (prevPlayer == this && curPlayer == this && !attackOnly) {
                  v add Move(MoveType.Strengthen, curLoc, prevLoc)
                  v add Move(MoveType.Strengthen, prevLoc, curLoc)
                } else if (prevPlayer != this && curPlayer == this && curHeight >= prevHeight) {
                  v add Move(MoveType.Attack, curLoc, prevLoc)
                } else if (prevPlayer == this && curPlayer != this && prevHeight >= curHeight) {
                  v add Move(MoveType.Attack, prevLoc, curLoc)
                }
              }
              prevLoc = curLoc
            }
          }
          curLoc += direction
        }
      }

      for (y <- Player.fullRange) discover(y * 9, Direction.NorthEast)
      for (x <- Player.fullRange) discover(x, Direction.South)
      for (y <- Player.halfRange) discover(y * 9, Direction.SouthEast)
      for (x <- Player.halfRange if x > 0) discover(x, Direction.SouthEast)
      MoveCache.add(board, attackOnly, v)
      v
    }
  }
}

object Player {
  private val fullRange = Array(0, 1, 2, 3, 4, 5, 6, 7, 8)
  private val halfRange = Array(0, 1, 2, 3, 4)

  case object White extends Player {
    val value: Int = 1
    val opponent = Player.Black
  }

  case object Black extends Player {
    val value: Int = -1
    val opponent = Player.White
  }

  def byValue(i: Int): Player = i match {
    case _ if i > 0 => White
    case _ if i < 0 => Black
    case _ => null
  }
}
