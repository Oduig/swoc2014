package com.gjos.scala.swoc.protocol

object Move {
  FastMove.init()

  def apply(moveType: MoveType, from: Location, to: Location): Move =
    if (moveType == MoveType.Pass) 0 else FastMove.encode(moveType)(from)(to)

  def moveType(m: Move): MoveType = FastMove.moveType(m)
  def from(m: Move): Location = FastMove.from(m)
  def to(m: Move): Location = FastMove.to(m)

  def toString(m: Move) = Location.label(from(m)) + s"--${moveType(m)}-->" + Location.label(to(m))
}