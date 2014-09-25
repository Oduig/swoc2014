package com.gjos.scala.swoc.protocol

object Location {

  def fromLabel(xi: String): Location = {
    val x = xi.head - 'A'
    val y = xi.last - '1' + Math.max(x - 4, 0)
    join(x, y)
  }

  def join(x: Int, y: Int): Location = FastBoard.diameter * y + x

  def label(index: Location): String = if (index >= 0) label(split(index)) else ""
  private def label(xy: (Int, Int)): String = {
    val xChar = 'A' + xy._1
    val yChar = '1' + xy._2 - Math.max(xy._1 - 4, 0)
    xChar.toChar.toString + yChar.toChar
  }
  def split(index: Location): (Int, Int) = (index % FastBoard.diameter, index / FastBoard.diameter)
}