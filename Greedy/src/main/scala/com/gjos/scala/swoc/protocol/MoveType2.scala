package com.gjos.scala.swoc.protocol

trait MoveType2 {
  val value: Int
}

object MoveType2 {
  case object Pass extends MoveType2 {
    val value = 0
  }
  case object Attack extends MoveType2 {
    val value = 1
  }
  case object Strengthen extends MoveType2 {
    val value = 2
  }

  def byValue(i: Int): MoveType2 = i match {
    case 0 => Pass
    case 1 => Attack
    case 2 => Strengthen
  }
}
