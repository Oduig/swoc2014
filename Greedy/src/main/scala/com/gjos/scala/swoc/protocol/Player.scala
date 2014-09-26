package com.gjos.scala.swoc.protocol

import java.util


object Player {
  private val fullRange = Array(0, 1, 2, 3, 4, 5, 6, 7, 8)
  private val halfRange = Array(0, 1, 2, 3, 4)

  val White = 1
  val Black = 2

  def opponent(p: Player) = if (p == White) Black else White

  def allValidMoves(p: Player, board: FastBoard, attackOnly: Boolean = false): Array[Move] = {
    if (MoveCache.hasKey(board, attackOnly)) {
      MoveCache.get(board, attackOnly)
    } else {
      val possibleMoves = new Array[Int](241)
      var i = 0
      if (!attackOnly) {
        possibleMoves(i) = Move(MoveType.Pass, -1, -1)
        i += 1
      }

      for (y <- Player.fullRange) i = discover(p, board, y * 9, (y + 1) * 9, Direction.NorthEast, i, possibleMoves, attackOnly)
      for (x <- Player.fullRange) i = discover(p, board, x, 81, Direction.South, i, possibleMoves, attackOnly)
      for (y <- Player.halfRange) i = discover(p, board, y * 9, 81, Direction.SouthEast, i, possibleMoves, attackOnly)
      for (x <- Player.halfRange if x > 0) i = discover(p, board, x, 81, Direction.SouthEast, i, possibleMoves, attackOnly)

      val moves = util.Arrays.copyOfRange(possibleMoves, 0, i)
      MoveCache.add(board, attackOnly, moves)
      moves
    }
  }


  private def discover(p: Player, board: FastBoard, start: Location, end: Location, direction: Direction, startIndex: Int, possibleMoves: Array[Int], attackOnly: Boolean): Int = {
    var curMoveIndex = startIndex
    var curLoc: Location = start
    var prevLoc: Location = -1
    while (curLoc < end) {
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
              possibleMoves(curMoveIndex) = Move(MoveType.Strengthen, curLoc, prevLoc)
              curMoveIndex += 1
              possibleMoves(curMoveIndex) = Move(MoveType.Strengthen, prevLoc, curLoc)
              curMoveIndex += 1
            } else if (prevPlayer != p && curPlayer == p && curHeight >= prevHeight) {
              possibleMoves(curMoveIndex) = Move(MoveType.Attack, curLoc, prevLoc)
              curMoveIndex += 1
            } else if (prevPlayer == p && curPlayer != p && prevHeight >= curHeight) {
              possibleMoves(curMoveIndex) = Move(MoveType.Attack, prevLoc, curLoc)
              curMoveIndex += 1
            }
          }
          prevLoc = curLoc
        }
      }
      curLoc += direction
    }
    curMoveIndex
  }
}
