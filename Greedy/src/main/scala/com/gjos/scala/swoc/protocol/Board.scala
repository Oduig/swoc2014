package com.gjos.scala.swoc.protocol

import scala.collection.mutable

class Board(private val state: mutable.Buffer[mutable.Buffer[Field]] = Board.defaultState) {

  def getField(location: BoardLocation): Field = getField(location.x, location.y)
  def getField(x: Int, y: Int): Field = state(x)(y)

  def setField(location: BoardLocation, field: Field) = {
    require(field.player.nonEmpty && field.stone.nonEmpty && field.height > 0, "Cannot set empty field")
    state(location.x)(location.y) = field
  }

  def clearField(location: BoardLocation) {
    state(location.x)(location.y) = Field.empty
  }

  def totalCount(player: Player, stone: Stone): Int = {
    state.map(
      row => row.count(field => field.player == Some(player) && field.stone == Some(stone))
    ).sum
  }

  def dump() {
    System.out.print("-- owners --------  -- stones --------  -- heights ----------------")
    for (y <- 0 until 9) {
      for (x <- 0 until 9) {
        val c = if (BoardLocation.IsLegal(x, y)) {
          state(x)(y).player match {
            case Some(Player.Black) => 'B'
            case Some(Player.White) => 'W'
            case _ => '.'
          }
        } else ' '
        System.out.print(" " + c)
      }
      System.out.print("  ")
      for (x <- 0 until 9) {
        val c = if (BoardLocation.IsLegal(x, y)) {
          state(x)(y).stone match {
            case Some(Stone.Pebble) => 'a'
            case Some(Stone.Rock) => 'b'
            case Some(Stone.Boulder) => 'c'
            case _ => '.'
          }
        } else ' '
        System.out.print(" " + c)
      }
      System.out.print("  ")
      for (x <- 0 until 9) {
        val s = if (BoardLocation.IsLegal(x, y)) f"${state(x)(y).height}%2d" else "  "
        System.out.print(" " + s)
      }
      System.out.println()
    }
    System.out.println("------------------  ------------------  ---------------------------")
    System.out.print("White: "
      + totalCount(Player.White, Stone.Pebble) + " a, "
      + totalCount(Player.White, Stone.Rock) + " b, "
      + totalCount(Player.White, Stone.Boulder) + " c")
    System.out.println(s"  Black: "
      + totalCount(Player.Black, Stone.Pebble) + " a, "
      + totalCount(Player.Black, Stone.Rock) + " b, "
      + totalCount(Player.Black, Stone.Boulder) + " c")
  }
}

object Board {
  import Field._
  private lazy val defaultState = mutable.Buffer(
    mutable.Buffer(whitePebble, whitePebble, whitePebble, whitePebble, blackPebble, empty, empty, empty, empty),
    mutable.Buffer(blackPebble, whiteRock, whiteRock, whiteRock, blackRock, blackPebble, empty, empty, empty),
    mutable.Buffer(blackPebble, blackRock, whiteBoulder, whiteBoulder, blackBoulder, blackRock, blackPebble, empty, empty),
    mutable.Buffer(blackPebble, blackRock, blackBoulder, whitePebble, blackPebble, blackBoulder, blackRock, blackPebble, empty),
    mutable.Buffer(blackPebble, blackRock, blackBoulder, blackPebble, empty, whitePebble, whiteBoulder, whiteRock, whitePebble),
    mutable.Buffer(empty, whitePebble, whiteRock, whiteBoulder, whitePebble, blackPebble, whiteBoulder, whiteRock, whitePebble),
    mutable.Buffer(empty, empty, whitePebble, whiteRock, whiteBoulder, blackBoulder, blackBoulder, whiteRock, whitePebble),
    mutable.Buffer(empty, empty, empty, whitePebble, whiteRock, blackRock, blackRock, blackRock, whitePebble),
    mutable.Buffer(empty, empty, empty, empty, whitePebble, blackPebble, blackPebble, blackPebble, blackPebble)
  )

  def fromInts(_state: Iterable[Iterable[Int]]) = {
    val state = (for (row <- _state) yield
      (for (cell <- row) yield Field.fromCode(cell)).toBuffer).toBuffer
    new Board(state)
  }
}