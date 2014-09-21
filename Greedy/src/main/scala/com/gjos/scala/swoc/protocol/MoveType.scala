package com.gjos.scala.swoc.protocol

trait MoveType {
  val value: Byte
}

object MoveType {
  case object Pass extends MoveType {
    val value: Byte = 0
  }
  case object Attack extends MoveType {
    val value: Byte = 1
  }
  case object Strengthen extends MoveType {
    val value: Byte = 2
  }

  def byValue(i: Byte): MoveType = i match {
    case 0 => Pass
    case 1 => Attack
    case 2 => Strengthen
  }
}
