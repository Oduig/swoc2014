package com.gjos.scala.swoc.protocol

trait Stone {
  val value: Byte
}

object Stone {
  case object Pebble extends Stone {
    val value: Byte = 1
  }

  case object Rock extends Stone {
    val value: Byte = 2
  }

  case object Boulder extends Stone {
    val value: Byte = 3
  }

  def byValue(i: Byte): Option[Stone] = i match {
    case 0 => None
    case 1 => Some(Pebble)
    case 2 => Some(Rock)
    case 3 => Some(Boulder)
  }
}