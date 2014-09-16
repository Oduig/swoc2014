package com.gjos.scala.swoc.protocol

case class Move(moveType: MoveType, from: Option[BoardLocation], to: Option[BoardLocation]) {
  override def toString = from.map(_.label).mkString + s"--$moveType-->" + to.map(_.label).mkString
}

object Move {
  def fromTo(board: Board, from: BoardLocation, to: BoardLocation) = {
    if (from == to) {
      new Move(MoveType.Pass, None, None)
    } else if (board.getField(from).player == board.getField(to).player) {
      new Move(MoveType.Strengthen, Some(from), Some(to))
    } else {
      new Move(MoveType.Attack, Some(from), Some(to))
    }
  }
}