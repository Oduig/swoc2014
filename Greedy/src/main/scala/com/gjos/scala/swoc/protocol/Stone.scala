package com.gjos.scala.swoc.protocol

trait Stone {
  val value: Int
}

object Stone {
  case object Pebble extends Stone {
    val value: Int = 1
  }

  case object Rock extends Stone {
    val value: Int = 2
  }

  case object Boulder extends Stone {
    val value: Int = 3
  }

  def byValue(i: Int): Stone = if (i == 0) null else if (i == 1) Pebble else if (i == 2) Rock else Boulder
}