package com.gjos.scala.swoc.protocol

import scala.collection.mutable
import com.gjos.scala.swoc.Score
import com.gjos.scala.swoc.protocol.Field._

class Board(private val state: mutable.Buffer[Byte] = Board.defaultState) {

  def getField(location: (Byte, Byte)): Field = getField(location._1, location._2)
  def getField(x: Byte, y: Byte): Field = state(y * Board.diameter + x)

  def score(us: Player) = Score.score(this, us)
  def stonesLeft(p: Player) = List(stoneValue(Stone.Pebble, p), stoneValue(Stone.Rock, p), stoneValue(Stone.Boulder, p))

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
        newBoard.setField(move.to.get, fromField.field)
      case MoveType.Strengthen =>
        val fromField = getField(move.from.get)
        newBoard.setField(move.from.get, Field.empty)
        newBoard.setField(move.to.get, Field.strengthened(fromField.field))
    }
    newBoard
  }

  def setField(location: (Byte, Byte), field: Byte) = {
    state(location._2 * Board.diameter + location._1) = field
  }

  def totalCount(player: Player, stone: Stone): Int = state count { field =>
    val f: Field = field
    f.player == Some(player) && f.stone == Some(stone)
  }

  def dump() {
    System.out.print("-- owners --------  -- stones --------  -- heights ----------------")
    for (y <- Board.fullRange) {
      for (x <- Board.fullRange) {
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
      for (x <- Board.fullRange) {
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
      for (x <- Board.fullRange) {
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
  private def stoneValue(s: Stone, p: Player): Float = (0f /: state) {
    (acc, field) =>
      val f: Field = field
      acc + (if (f.player == Some(p) && f.stone == Some(s)) f.height * heightMultiplier else 0f)
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

  def fromBytes(_state: Iterable[Iterable[Byte]]) = {
    val state = for {
      row <- _state
      field <- row
    } yield field
    new Board(state.toBuffer)
  }

  private val diameter: Byte = 9
  private val radius: Byte = 5
  val halfRange = List.tabulate(radius)(_.toByte)
  val fullRange: List[Byte] = halfRange ++ List.tabulate(radius)(index => (index + radius).toByte)
}