package com.gjos.scala.swoc.protocol

trait Player {
  val value: Int
  val opponent: Player

  def allValidMoves(board: Board): Vector[Move] = {
    for {
      fromLocation <- BoardLocation.allValidBoardLocations
      fromField = board.getField(fromLocation)
      if fromField.player == Some(this)
      toLocation <- board.getPossibleToLocations(fromLocation)
      toField = board.getField(toLocation)
      if fromField.height >= toField.height || toField.player == Some(this)
    } yield Move.fromTo(board, fromLocation, toLocation)
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
