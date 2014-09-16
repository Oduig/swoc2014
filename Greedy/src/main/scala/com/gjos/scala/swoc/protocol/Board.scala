package com.gjos.scala.swoc.protocol

import scala.collection.mutable
import com.gjos.scala.swoc.{Direction, Score}

class Board(private val state: mutable.Buffer[mutable.Buffer[Field]] = Board.defaultState) {

  def getField(location: BoardLocation): Field = getField(location.x, location.y)
  def getField(x: Int, y: Int): Field = state(y)(x)

  def score(us: Player) = Score.score(this, us)
  def stonesLeft(p: Player) = List(pebbleValue(p), rockValue(p), boulderValue(p))

  private val heightMultiplier = 1.1f
  private def stoneValue(s: Stone)(p: Player) = sumFieldsBy { field =>
    if (field.player == Some(p) && field.stone == Some(s)) field.height * heightMultiplier else 0
  }

  val pebbleValue: Player => Float = stoneValue(Stone.Pebble)
  val rockValue: Player => Float = stoneValue(Stone.Rock)
  val boulderValue: Player => Float = stoneValue(Stone.Boulder)

  def sumFieldsBy[T: Numeric](predicate: Field => T): T = {
    state.map(
      row => row.map(predicate).sum
    ).sum
  }

  def copy() = {
    val newState = mutable.Buffer[mutable.Buffer[Field]]()
    for (row <- state) yield {
      val newRow = mutable.Buffer[Field]()
      newState.append(newRow)
      for (field <- row) newRow.append(field)
    }
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

  def getPossibleToLocations(fromLocation: BoardLocation): List[BoardLocation] = {
    val targets = Direction.allDirections.map(getFirstNonEmptyInDirection(fromLocation, _))

    val validDirections = targets collect {
      case Some(target) if isValidMove(fromLocation, target) => target
    }
    fromLocation :: validDirections
  }

  def getFirstNonEmptyInDirection(location: BoardLocation, direction: Direction): Option[BoardLocation] = {
    var x: Int = location.x
    var y: Int = location.y
    do {
      x += direction.x
      y += direction.y
    } while (BoardLocation.IsValid(x, y) && getField(x, y).player.isEmpty)

    if (!BoardLocation.IsValid(x, y)) {
      None
    } else {
      val newLocation: BoardLocation = new BoardLocation(x, y)
      if (newLocation == location || getField(newLocation).player.isEmpty) {
        None
      } else {
        Some(newLocation)
      }
    }
  }

  def isValidMove(from: BoardLocation, to: BoardLocation): Boolean = {
    val fromOwner = getField(from).player
    val toOwner = getField(to).player
    val fromHeight: Int = getField(from).height
    val toHeight: Int = getField(to).height
    (fromOwner != None) && (toOwner != None) && ((fromOwner == toOwner) || fromHeight >= toHeight)
  }

  def setField(location: BoardLocation, field: Field) = {
    state(location.y)(location.x) = field
  }

  def totalCount(player: Player, stone: Stone): Int =
    sumFieldsBy(field => if (field.player == Some(player) && field.stone == Some(stone)) 1 else 0)

  def dump() {
    System.out.print("-- owners --------  -- stones --------  -- heights ----------------")
    for (y <- 0 until 9) {
      for (x <- 0 until 9) {
        val c = if (BoardLocation.IsValid(x, y)) {
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
        val c = if (BoardLocation.IsValid(x, y)) {
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
        val s = if (BoardLocation.IsValid(x, y)) f"${state(y)(x).height}%2d" else "  "
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