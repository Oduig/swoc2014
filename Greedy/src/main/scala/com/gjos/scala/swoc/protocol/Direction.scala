package com.gjos.scala.swoc.protocol

object Direction {
  val North = -9
  val South = 9
  val NorthEast = 1
  val SouthEast = 10
  val NorthWest = -10
  val SouthWest = -1

  def allDirections = List(North, NorthEast, SouthEast, South, SouthWest, NorthWest)
}
