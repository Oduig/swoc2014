package com.gjos.scala.swoc.protocol

trait MoveType {
  val value: Int
}

object MoveType {
  case object Pass extends MoveType {
    val value = 0
  }
  case object Attack extends MoveType {
    val value = 1
  }
  case object Strengthen extends MoveType {
    val value = 2
  }

  def byValue(i: Int): MoveType = i match {
    case 0 => Pass
    case 1 => Attack
    case 2 => Strengthen
  }
}
