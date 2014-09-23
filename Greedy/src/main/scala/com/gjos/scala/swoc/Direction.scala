package com.gjos.scala.swoc

trait Direction {
  val x: Int
  val y: Int
}

object Direction {
  case object North extends Direction { val x: Int = 0; val y: Int = -1 }
  case object South extends Direction { val x: Int = 0; val y: Int = 1 }
  case object NorthEast extends Direction { val x: Int = 1; val y: Int = 0 }
  case object SouthEast extends Direction { val x: Int = 1; val y: Int = 1 }
  case object NorthWest extends Direction { val x: Int = -1; val y: Int = -1 }
  case object SouthWest extends Direction { val x: Int = -1; val y: Int = 0 }

  def allDirections = List(North, NorthEast, SouthEast, South, SouthWest, NorthWest)
}
