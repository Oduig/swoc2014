package com.gjos.scala.swoc.protocol

case class BoardLocation2(x: Int, y: Int) {
  require(BoardLocation2.IsLegal(x, y), "not a legal board location")

  def ToLabel(): String = {
    val xChar = 'A' + x
    val yChar = '1' + y - Math.max(x - 4, 0)
    (xChar.toChar + yChar.toChar).toString
  }
}

object BoardLocation2 {
  def IsLegal(x: Int, y: Int) = {
    x >= 0 && x < 9 && y >= 0 && y < 9 &&
      (x - y) < 5 && (y - x) < 5 && (x != 4 || y != 4)
  }
}