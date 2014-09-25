package com.gjos.scala.swoc.protocol

object Move {
  // Encoding:
  // 0 == Pass
  // movetype from to
  // 1|2 00-80 00-80
  // e.g. 24105
  def apply(moveType: MoveType, from: Location, to: Location): Move =
    if (moveType == MoveType.Pass) 0 else moveType.value * 10000 + from * 100 + to

  def moveType(m: Move): MoveType = MoveType.byValue(m / 10000)
  def from(m: Move): Location = (m % 10000) / 100
  def to(m: Move): Location = m % 100

  def isPass(m: Move) = m == 0
  def isAttack(m: Move) = m / 10000 == 1
  def isStrengthen(m: Move) = m / 10000 == 2

  def toString(m: Move) = Location.label(from(m)) + s"--${moveType(m)}-->" + Location.label(to(m))
}