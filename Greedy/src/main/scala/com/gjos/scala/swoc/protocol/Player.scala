package com.gjos.scala.swoc.protocol

trait Player {
  val value: Int
  val opponent: Player

  def allValidMoves(board: Board, attackOnly: Boolean = false): Vector[Move] = {
    var v = if (attackOnly) Vector.empty[Move] else Vector(Move(MoveType.Pass, -1, -1))

    def discover(start: Location, direction: Direction) {
      var curLoc: Location = start
      var prevLoc: Location = -1
      while (curLoc < 9 * 9) {
        if (!Location.isValid(curLoc)) {
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
                v = v :+ Move(MoveType.Strengthen, curLoc, prevLoc)
                v = v :+ Move(MoveType.Strengthen, prevLoc, curLoc)
              } else if (prevPlayer != this && curPlayer == this && curHeight >= prevHeight) {
                v = v :+ Move(MoveType.Attack, curLoc, prevLoc)
              } else if (prevPlayer == this && curPlayer != this && prevHeight >= curHeight) {
                v = v :+ Move(MoveType.Attack, prevLoc, curLoc)
              }
            }
            prevLoc = curLoc
          }
        }
        curLoc += direction
      }
    }

    for (y <- Board.fullRange) discover(y * 9, Direction.NorthEast)
    for (x <- Board.fullRange) discover(x, Direction.South)
    for (y <- Board.halfRange) discover(y * 9, Direction.SouthEast)
    for (x <- Board.halfRange if x > 0) discover(x, Direction.SouthEast)
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

  def byValue(i: Int): Player = i match {
    case _ if i > 0 => White
    case _ if i < 0 => Black
    case _ => null
  }
}
