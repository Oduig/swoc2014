package com.gjos.scala.swoc.protocol

import scala.collection.mutable

class Board(private val state: mutable.Buffer[mutable.Buffer[Field]] = Board.defaultState) {

  def getField(location: BoardLocation): Field = getField(location.x, location.y)
  def getField(x: Int, y: Int): Field = state(y)(x)

  /**
   * Returns the current board's utility from a given player's perspective.
   * Rates the board by the difference between us and them,
   * where score is determined by how close we are to having a type of stones eliminated
   */
  def score(us: Player): Float = {
    val them = if (us == Player.Black) Player.White else Player.Black

    val myScore = stoneScore(us).min
    val theirScore = stoneScore(them).min

    myScore - theirScore
  }

  private val heightMultiplier = 1f
  private def stoneCount(s: Stone)(p: Player) = scoreFields { field =>
    if (field.player == Some(p) && field.stone == Some(s)) field.height * heightMultiplier else 0
  }
  private val pebbleCount: Player => Float = stoneCount(Stone.Pebble)
  private val rockCount: Player => Float = stoneCount(Stone.Rock)
  private val boulderCount: Player => Float = stoneCount(Stone.Boulder)
  private def stoneScore(p: Player) = List(pebbleCount(p) / 15f, rockCount(p) / 9f, boulderCount(p) / 5f)

  private def scoreFields[T: Numeric](predicate: Field => T): T = {
    state.map(
      row => row.map(predicate).sum
    ).sum
  }

  def copy() = new Board(state)
  def applyMove(move: Move): Board = {
    val newBoard = copy()
    move.moveType match {
      case MoveType.Pass => newBoard
      case MoveType.Attack =>
        val fromField = getField(move.from.get)
        newBoard.setField(move.from.get, Field.empty)
        newBoard.setField(move.to.get, Field(fromField.player, fromField.stone, fromField.height))
      case MoveType.Strengthen =>
        val fromField = getField(move.from.get)
        newBoard.setField(move.from.get, Field.empty)
        newBoard.setField(move.to.get, Field(fromField.player, fromField.stone, fromField.height + 1))
    }
    newBoard
  }

  def setField(location: BoardLocation, field: Field) = {
    state(location.x)(location.y) = field
  }

  def totalCount(player: Player, stone: Stone): Int =
    scoreFields(field => if (field.player == Some(player) && field.stone == Some(stone)) 1 else 0)

  def dump() {
    System.out.print("-- owners --------  -- stones --------  -- heights ----------------")
    for (y <- 0 until 9) {
      for (x <- 0 until 9) {
        val c = if (BoardLocation.IsLegal(x, y)) {
          state(y)(x).player match {
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
          state(y)(x).stone match {
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
        val s = if (BoardLocation.IsLegal(x, y)) f"${state(y)(x).height}%2d" else "  "
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