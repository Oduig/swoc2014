package com.gjos.scala.swoc.protocol

case class Move(moveType: MoveType, from: (Int, Int), to: (Int, Int)) {
  override def toString = {
    (if (from != null) BoardLocation.label(from) else "") +
      s"--$moveType-->" +
      (if (to != null) BoardLocation.label(to) else "")
  }
}