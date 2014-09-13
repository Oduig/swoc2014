package com.gjos.scala.swoc.protocol

case class Move(moveType: MoveType, from: Option[BoardLocation], to: Option[BoardLocation])
