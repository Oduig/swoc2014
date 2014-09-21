package com.gjos.scala.swoc

trait Direction {
  val x: Byte
  val y: Byte
}

object Direction {
  case object North extends Direction { val x: Byte = 0; val y: Byte = -1 }
  case object South extends Direction { val x: Byte = 0; val y: Byte = 1 }
  case object NorthEast extends Direction { val x: Byte = 1; val y: Byte = 0 }
  case object SouthEast extends Direction { val x: Byte = 1; val y: Byte = 1 }
  case object NorthWest extends Direction { val x: Byte = -1; val y: Byte = -1 }
  case object SouthWest extends Direction { val x: Byte = -1; val y: Byte = 0 }

  def allDirections = List(North, NorthEast, SouthEast, South, SouthWest, NorthWest)
}
