package com.gjos.scala.swoc.protocol

import scala.collection.mutable
import com.gjos.scala.swoc.{Direction, Score}
import com.gjos.scala.swoc.util.Stopwatch

class Board(private val state: mutable.Buffer[Field] = Board.defaultState) {
  private val rowSize = 9

  def getField(location: (Int, Int)): Field = getField(location._1, location._2)
  def getField(x: Int, y: Int): Field = state(y * rowSize + x)

  def score(us: Player) = Score.score(this, us)
  def stonesLeft(p: Player) = List(pebbleValue(p), rockValue(p), boulderValue(p))

  val pebbleValue: Player => Float = stoneValue(Stone.Pebble)
  val rockValue: Player => Float = stoneValue(Stone.Rock)
  val boulderValue: Player => Float = stoneValue(Stone.Boulder)

  private def sumFieldsBy[T: Numeric](predicate: Field => T): T = {
    state.map(predicate).sum
  }

  private def copy() = {
    val newState = for (field <- state) yield field
    new Board(newState)
  }

  def applyMove(move: Move): Board = {
    val newBoard = copy()
    move.moveType match {
      case MoveType.Pass => newBoard
      case MoveType.Attack =>
        val fromField = getField(move.from.get)
        newBoard.setField(move.from.get, Field.empty)
        newBoard.setField(move.to.get, fromField)
      case MoveType.Strengthen =>
        val fromField = getField(move.from.get)
        newBoard.setField(move.from.get, Field.empty)
        newBoard.setField(move.to.get, fromField.higher)
    }
    newBoard
  }

  def setField(location: (Int, Int), field: Field) = {
    state(location._2 * rowSize + location._1) = field
  }

  def totalCount(player: Player, stone: Stone): Int =
    sumFieldsBy(field => if (field.player == Some(player) && field.stone == Some(stone)) 1 else 0)

  def dump() {
    System.out.print("-- owners --------  -- stones --------  -- heights ----------------")
    for (y <- 0 until 9) {
      for (x <- 0 until 9) {
        val c = if (BoardLocation.IsValid(x, y)) {
          getField(x, y).player match {
            case Some(Player.Black) => 'B'
            case Some(Player.White) => 'W'
            case _ => '.'
          }
        } else ' '
        System.out.print(" " + c)
      }
      System.out.print("  ")
      for (x <- 0 until 9) {
        val c = if (BoardLocation.IsValid(x, y)) {
          getField(x, y).stone match {
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
        val s = if (BoardLocation.IsValid(x, y)) f"${getField(x, y).height}%2d" else "  "
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


  private val heightMultiplier = 1.1f
  private def stoneValue(s: Stone)(p: Player) = sumFieldsBy { field =>
    if (field.player == Some(p) && field.stone == Some(s)) field.height * heightMultiplier else 0
  }
}

object Board {
  import Field._
  private lazy val defaultState = mutable.Buffer(
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
      cell <- row
    } yield Field.fromCode(cell)
    new Board(state.toBuffer)
  }
}