package com.gjos.scala.swoc.protocol

case class Move(moveType: MoveType, from: Option[(Int, Int)], to: Option[(Int, Int)]) {
  override def toString = from.map(BoardLocation.label).mkString + s"--$moveType-->" + to.map(BoardLocation.label).mkString
}