package com.gjos.scala.swoc.protocol

case class Move(moveType: MoveType, from: Option[(Byte, Byte)], to: Option[(Byte, Byte)]) {
  override def toString = from.map(BoardLocation.label).mkString + s"--$moveType-->" + to.map(BoardLocation.label).mkString
}