package com.gjos.scala.swoc.protocol

object Location {
  private val validLocations: Set[Location] = {
    for {
      x <- Board.fullRange
      y <- Board.fullRange
      if (x - y) < 5 && (y - x) < 5 && (x != 4 || y != 4)
    } yield join(x, y)
  }.toSet

  def isValid(l: Location): Boolean = validLocations contains l
  def isValid(x: Int, y: Int): Boolean = isValid(join(x, y))

  def fromLabel(xi: String): Location = {
    val x = xi.head - 'A'
    val y = xi.last - '1' + Math.max(x - 4, 0)
    join(x, y)
  }

  def join(x: Int, y: Int): Location = Board.diameter * y + x

  def label(index: Location): String = if (index >= 0) label(split(index)) else ""
  private def label(xy: (Int, Int)): String = {
    val xChar = 'A' + xy._1
    val yChar = '1' + xy._2 - Math.max(xy._1 - 4, 0)
    xChar.toChar.toString + yChar.toChar
  }
  def split(index: Location): (Int, Int) = (index % Board.diameter, index / Board.diameter)
}