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
}
