package com.gjos.scala.swoc.protocol

import scala.collection.mutable.ArrayBuffer

class Board2() {

  import Field._

  private val state = ArrayBuffer(
    ArrayBuffer(whitePebble, whitePebble, whitePebble, whitePebble, blackPebble, empty, empty, empty, empty),
    ArrayBuffer(blackPebble, whiteRock, whiteRock, whiteRock, blackRock, blackPebble, empty, empty, empty),
    ArrayBuffer(blackPebble, blackRock, whiteBoulder, whiteBoulder, blackBoulder, blackRock, blackPebble, empty, empty),
    ArrayBuffer(blackPebble, blackRock, blackBoulder, whitePebble, blackPebble, blackBoulder, blackRock, blackPebble, empty),
    ArrayBuffer(blackPebble, blackRock, blackBoulder, blackPebble, empty, whitePebble, whiteBoulder, whiteRock, whitePebble),
    ArrayBuffer(empty, whitePebble, whiteRock, whiteBoulder, whitePebble, blackPebble, whiteBoulder, whiteRock, whitePebble),
    ArrayBuffer(empty, empty, whitePebble, whiteRock, whiteBoulder, blackBoulder, blackBoulder, whiteRock, whitePebble),
    ArrayBuffer(empty, empty, empty, whitePebble, whiteRock, blackRock, blackRock, blackRock, whitePebble),
    ArrayBuffer(empty, empty, empty, empty, whitePebble, blackPebble, blackPebble, blackPebble, blackPebble)
  )

  def getField(location: BoardLocation2) = getField(location.x, location.y)
  def getField(x: Int, y: Int) = state(x)(y)

  def setField(location: BoardLocation2, field: Field) = {
    require(field.player.nonEmpty && field.stone.nonEmpty && field.height > 0, "Cannot set empty field")
    state(location.x)(location.y) = field
  }

  def clearField(location: BoardLocation2) {
    state(location.x)(location.y) = empty
  }

  def totalCount(player: Player2, stone: Stone2): Int = {
    state.map(
      row => row.count(field => field.player == Some(player) && field.stone == Some(stone))
    ).sum
  }

  def dump() {
    System.out.print("-- owners --------  ")
    System.out.print("-- stones --------  ")
    System.out.println("-- heights ----------------")
    for (y <- 0 until 9) {
      for (x <- 0 until 9) {
        val c = if (BoardLocation2.IsLegal(x, y)) {
          state(x)(y).player match {
            case Some(Player2.Black) => 'B'
            case Some(Player2.White) => 'W'
            case _ => '.'
          }
        } else ' '
        System.out.print(" " + c)
      }
      System.out.print("  ")
      for (x <- 0 until 9) {
        val c = if (BoardLocation2.IsLegal(x, y)) {
          state(x)(y).stone match {
            case Some(Stone2.Pebble) => 'a'
            case Some(Stone2.Rock) => 'b'
            case Some(Stone2.Boulder) => 'c'
            case _ => '.'
          }
        } else ' '
        System.out.print(" " + c)
      }
      System.out.print("  ")
      for (x <- 0 until 9) {
        val s = if (BoardLocation2.IsLegal(x, y)) String.format("%2d", state(x)(y).height) else "  "
        System.out.print(" " + s)
      }
      System.out.println()
    }
    System.out.print("------------------  ")
    System.out.print("------------------  ")
    System.out.println("---------------------------")
    System.out.print("White: "
      + totalCount(Player2.White, Stone2.Pebble) + " a, "
      + totalCount(Player2.White, Stone2.Rock) + " b, "
      + totalCount(Player2.White, Stone2.Boulder) + " c")
    System.out.println("  Black: "
      + totalCount(Player2.Black, Stone2.Pebble) + " a, "
      + totalCount(Player2.Black, Stone2.Rock) + " b, "
      + totalCount(Player2.Black, Stone2.Boulder) + " c")
  }
}
