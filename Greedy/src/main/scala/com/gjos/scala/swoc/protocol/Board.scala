package com.gjos.scala.swoc.protocol

import java.util

class Board(private val state: Array[Field] = Board.defaultState) {

  def getField(location: Location): Field = state(location)

  def score(us: Player) = Score.score(this, us)
  def iterator = state.iterator
  def myHashCode: BoardHash = util.Arrays.hashCode(state)

  private def copy() = {
    val newState = for (field <- state) yield field
    new Board(newState)
  }

  def applyMove(move: Move): Board = {
    val newBoard = copy()
    Move.moveType(move) match {
      case MoveType.Pass => newBoard
      case MoveType.Attack =>
        val fromField = getField(Move.from(move))
        newBoard.setField(Move.from(move), Field.empty)
        newBoard.setField(Move.to(move), fromField)
      case MoveType.Strengthen =>
        val fromField = getField(Move.from(move))
        val toField = getField(Move.to(move))
        newBoard.setField(Move.from(move), Field.empty)
        newBoard.setField(Move.to(move), Field.strengthened(fromField, toField))
    }
    newBoard
  }

  def setField(location: Location, field: Field) {
    state(location) = field
  }

  private def totalCount(player: Player, stone: Stone): Int = state count { field =>
    Field.player(field) == player && Field.stone(field) == stone
  }

  def dump() {
    System.out.println("-- owners --------  -- stones --------  -- heights ----------------")
    for (y <- Board.fullRange) {
      for (x <- Board.fullRange) {
        val c = if (Location.isValid(x, y)) {
          Field.player(getField(Location.join(x, y))) match {
            case Player.Black => 'B'
            case Player.White => 'W'
            case _ => '.'
          }
        } else ' '
        System.out.print(" " + c)
      }
      System.out.print("  ")
      for (x <- Board.fullRange) {
        val c = if (Location.isValid(x, y)) {
          Field.stone(getField(Location.join(x, y))) match {
            case Stone.Pebble => 'a'
            case Stone.Rock => 'b'
            case Stone.Boulder => 'c'
            case _ => '.'
          }
        } else ' '
        System.out.print(" " + c)
      }
      System.out.print("  ")
      for (x <- Board.fullRange) {
        val s = if (Location.isValid(x, y)) f"${Field.height(getField(Location.join(x, y)))}%2d" else "  "
        System.out.print(" " + s)
      }
      println()
    }
    println("------------------  ------------------  ---------------------------")
    System.out.print("White: "
      + totalCount(Player.White, Stone.Pebble) + " a, "
      + totalCount(Player.White, Stone.Rock) + " b, "
      + totalCount(Player.White, Stone.Boulder) + " c")
    println(s"  Black: "
      + totalCount(Player.Black, Stone.Pebble) + " a, "
      + totalCount(Player.Black, Stone.Rock) + " b, "
      + totalCount(Player.Black, Stone.Boulder) + " c")
  }

}

object Board {
  import Field._
  private lazy val defaultState = Array(
    whitePebble, whitePebble, whitePebble, whitePebble, blackPebble, empty, empty, empty, empty,
    blackPebble, whiteRock, whiteRock, whiteRock, blackRock, blackPebble, empty, empty, empty,
    blackPebble, blackRock, whiteBoulder, whiteBoulder, blackBoulder, blackRock, blackPebble, empty, empty,
    blackPebble, blackRock, blackBoulder, whitePebble, blackPebble, blackBoulder, blackRock, blackPebble, empty,
    blackPebble, blackRock, blackBoulder, blackPebble, empty, whitePebble, whiteBoulder, whiteRock, whitePebble,
    empty, whitePebble, whiteRock, whiteBoulder, whitePebble, blackPebble, whiteBoulder, whiteRock, whitePebble,
    empty, empty, whitePebble, whiteRock, whiteBoulder, blackBoulder, blackBoulder, whiteRock, whitePebble,
    empty, empty, empty, whitePebble, whiteRock, blackRock, blackRock, blackRock, whitePebble,
    empty, empty, empty, empty, whitePebble, blackPebble, blackPebble, blackPebble, blackPebble
  )

  def fromInts(_state: Iterable[Iterable[Field]]) = {
    val state = for {
      row <- _state
      field <- row
    } yield field
    new Board(state.toArray)
  }

  val diameter = 9
  private val radius = 5
  val halfRange = List.tabulate(radius)(identity)
  val fullRange = List.tabulate(diameter)(identity)
}