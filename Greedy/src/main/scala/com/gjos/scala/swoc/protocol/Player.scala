package com.gjos.scala.swoc.protocol

import java.util


object Player {
  private val fullRange = Array(0, 1, 2, 3, 4, 5, 6, 7, 8)
  private val halfRange = Array(0, 1, 2, 3, 4)

  val White = 1
  val Black = 2

  def opponent(p: Player) = if (p == White) Black else White

  def allValidMoves(p: Player, board: FastBoard, attackOnly: Boolean = false): util.ArrayList[Move] = {
    if (MoveCache.hasKey(board, attackOnly)) {
      MoveCache.get(board, attackOnly)
    } else {
      val v = new util.ArrayList[Move]()
      if (!attackOnly) v.add(Move(MoveType.Pass, -1, -1))

      for (y <- Player.fullRange) discover(p, board, y * 9, Direction.NorthEast, v, attackOnly)
      for (x <- Player.fullRange) discover(p, board, x, Direction.South, v, attackOnly)
      for (y <- Player.halfRange) discover(p, board, y * 9, Direction.SouthEast, v, attackOnly)
      for (x <- Player.halfRange if x > 0) discover(p, board, x, Direction.SouthEast, v, attackOnly)
      MoveCache.add(board, attackOnly, v)
      v
    }
  }

  private def discover(p: Player, board: FastBoard, start: Location, direction: Direction, v: util.ArrayList[Move], attackOnly: Boolean) {
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
            if (prevPlayer == p && curPlayer == p && !attackOnly) {
              v add Move(MoveType.Strengthen, curLoc, prevLoc)
              v add Move(MoveType.Strengthen, prevLoc, curLoc)
            } else if (prevPlayer != p && curPlayer == p && curHeight >= prevHeight) {
              v add Move(MoveType.Attack, curLoc, prevLoc)
            } else if (prevPlayer == p && curPlayer != p && prevHeight >= curHeight) {
              v add Move(MoveType.Attack, prevLoc, curLoc)
            }
          }
          prevLoc = curLoc
        }
      }
      curLoc += direction
    }
  }
}
