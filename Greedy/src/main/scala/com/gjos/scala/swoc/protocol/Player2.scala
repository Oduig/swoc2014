package com.gjos.scala.swoc.protocol

trait Player2 {
  val value: Int
}

object Player2 {
  case object White extends Player2 {
    val value = 1
  }

  case object Black extends Player2 {
    val value = -1
  }

  def byValue(i: Int): Option[Player2] = i match {
    case _ if i > 0 => Some(White)
    case _ if i < 0 => Some(Black)
    case _ => None
  }
}
