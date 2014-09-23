package com.gjos.scala.swoc.protocol

object BoardLocation {
  def IsValid(x: Int, y: Int) = {
    x >= 0 && x < 9 && y >= 0 && y < 9 &&
      (x - y) < 5 && (y - x) < 5 && (x != 4 || y != 4)
  }

  def fromLabel(xi: String) = {
    val x = xi.head - 'A'
    val y = xi.last - '1' + Math.max(x - 4, 0)
    (x, y)
  }

  def label(bl: (Int, Int)): String = {
    val xChar = 'A' + bl._1
    val yChar = '1' + bl._2 - Math.max(bl._1 - 4, 0)
    xChar.toChar.toString + yChar.toChar
  }
}