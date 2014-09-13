package com.gjos.scala.swoc.protocol

trait Player {
  val value: Int
}

object Player {
  case object White extends Player {
    val value = 1
  }

  case object Black extends Player {
    val value = -1
  }

  def byValue(i: Int): Option[Player] = i match {
    case _ if i > 0 => Some(White)
    case _ if i < 0 => Some(Black)
    case _ => None
  }
}
