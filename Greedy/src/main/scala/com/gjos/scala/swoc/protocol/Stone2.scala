package com.gjos.scala.swoc.protocol

trait Stone2 {
  val value: Int
}

object Stone2 {
  case object Pebble extends Stone2 {
    val value = 1
  }

  case object Rock extends Stone2 {
    val value = 2
  }

  case object Boulder extends Stone2 {
    val value = 3
  }

  def byValue(i: Int): Option[Stone2] = i match {
    case 0 => None
    case 1 => Some(Pebble)
    case 2 => Some(Rock)
    case 3 => Some(Boulder)
  }
}