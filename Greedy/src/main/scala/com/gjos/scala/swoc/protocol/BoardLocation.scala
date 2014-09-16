package com.gjos.scala.swoc.protocol

case class BoardLocation(x: Int, y: Int) {
  require(BoardLocation.IsValid(x, y), "not a valid board location")

  def label: String = {
    val xChar = 'A' + x
    val yChar = '1' + y - Math.max(x - 4, 0)
    xChar.toChar.toString + yChar.toChar
  }
}

object BoardLocation {
  def IsValid(x: Int, y: Int) = {
    x >= 0 && x < 9 && y >= 0 && y < 9 &&
      (x - y) < 5 && (y - x) < 5 && (x != 4 || y != 4)
  }

  def fromLabel(xi: String) = {
    val x = xi.head - 'A'
    val y = xi.last - '1' + Math.max(x - 4, 0)
    BoardLocation(x, y)
  }

  lazy val allValidBoardLocations: Vector[BoardLocation] = {
    for {
      y <- (0 until 9).toVector
      x <- (0 until 9).toVector
      if IsValid(x, y)
    } yield new BoardLocation(x, y)
  }
}