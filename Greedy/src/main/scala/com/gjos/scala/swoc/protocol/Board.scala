package com.gjos.scala.swoc.protocol

import scala.collection.mutable
import com.gjos.scala.swoc.Score
import com.gjos.scala.swoc.protocol.Field._

class Board(private val state: Array[Int] = Board.defaultState) {

  def getField(location: (Int, Int)): Int = getField(location._1, location._2)
  def getField(x: Int, y: Int): Int = state(y * Board.diameter + x)

  def score(us: Player) = Score.score(this, us)
  def iterator = state.iterator

  private def copy() = {
    val newState = for (field <- state) yield field
    new Board(newState)
  }

  def applyMove(move: Move): Board = {
    val newBoard = copy()
    move.moveType match {
      case MoveType.Pass => newBoard
      case MoveType.Attack =>
        val fromField = getField(move.from)
        newBoard.setField(move.from, Field.empty)
        newBoard.setField(move.to, fromField)
      case MoveType.Strengthen =>
        val fromField = getField(move.from)
        val toField = getField(move.to)
        newBoard.setField(move.from, Field.empty)
        newBoard.setField(move.to, Field.strengthened(fromField, toField))
    }
    newBoard
  }

  def setField(location: (Int, Int), field: Int) = {
    state(location._2 * Board.diameter + location._1) = field
  }


  private def totalCount(player: Player, stone: Stone): Int = state count { field =>
    Field.player(field) == Some(player) && Field.stone(field) == Some(stone)
  }

  def dump() {
    System.out.println("-- owners --------  -- stones --------  -- heights ----------------")
    for (y <- Board.fullRange) {
      for (x <- Board.fullRange) {
        val c = if (BoardLocation.IsValid(x, y)) {
          Field.player(getField(x, y)) match {
            case Some(Player.Black) => 'B'
            case Some(Player.White) => 'W'
            case _ => '.'
          }
        } else ' '
        System.out.print(" " + c)
      }
      System.out.print("  ")
      for (x <- Board.fullRange) {
        val c = if (BoardLocation.IsValid(x, y)) {
          Field.stone(getField(x, y)) match {
            case Some(Stone.Pebble) => 'a'
            case Some(Stone.Rock) => 'b'
            case Some(Stone.Boulder) => 'c'
            case _ => '.'
          }
        } else ' '
        System.out.print(" " + c)
      }
      System.out.print("  ")
      for (x <- Board.fullRange) {
        val s = if (BoardLocation.IsValid(x, y)) f"${Field.height(getField(x, y))}%2d" else "  "
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

  def fromInts(_state: Iterable[Iterable[Int]]) = {
    val state = for {
      row <- _state
      field <- row
    } yield field
    new Board(state.toArray)
  }

  private val diameter = 9
  private val radius = 5
  val halfRange = List.tabulate(radius)(identity)
  val fullRange = List.tabulate(diameter)(identity)
}