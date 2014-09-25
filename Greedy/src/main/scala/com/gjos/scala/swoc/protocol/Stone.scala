package com.gjos.scala.swoc.protocol

trait Stone {
  val value: Int
  val isPebble: Boolean
  val isRock: Boolean
}

object Stone {
  case object Pebble extends Stone {
    val value: Int = 1
    val isPebble = true
    val isRock = false
  }

  case object Rock extends Stone {
    val value: Int = 2
    val isPebble = false
    val isRock = true
  }

  case object Boulder extends Stone {
    val value: Int = 3
    val isPebble = false
    val isRock = false
  }

  def byValue(i: Int): Stone = if (i == 0) null else if (i == 1) Pebble else if (i == 2) Rock else Boulder
}