package com.gjos.scala.swoc.protocol

trait Stone {
  val value: Int
}

object Stone {
  case object Pebble extends Stone {
    val value = 1
  }

  case object Rock extends Stone {
    val value = 2
  }

  case object Boulder extends Stone {
    val value = 3
  }

  def byValue(i: Int): Option[Stone] = i match {
    case 0 => None
    case 1 => Some(Pebble)
    case 2 => Some(Rock)
    case 3 => Some(Boulder)
  }
}