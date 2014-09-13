package com.gjos.scala.swoc

import java.util.Random
import com.gjos.scala.swoc.protocol._
import scala.collection.mutable.ListBuffer
import com.gjos.scala.swoc.protocol.InitiateRequest

class Bot(private var myColor: Option[Player]) extends IBot {
  private val random = new Random

  def handleInitiate(request: InitiateRequest) {
    myColor = Some(request.color)
  }

  def handleMove(request: MoveRequest): Move = request.allowedMoves match {
    case x :: Nil if x == MoveType.Attack => getRandomAttack(request.board)
    case _ => getRandomMove(request.board)
  }

  def handleProcessedMove(move: ProcessedMove) {
  }

  private def getRandomMove(board: Board): Move = {
    val allLocations: Vector[BoardLocation] = Bot.allLegalBoardLocations().toVector

    val myLocations: Vector[BoardLocation] = for {
      location <- allLocations
      if board.getField(location).player == myColor
    } yield location

    val fromLocation: BoardLocation = myLocations(random.nextInt(myLocations.size))
    val possibleToLocations: List[BoardLocation] = Bot.getPossibleToLocations(board, fromLocation)
    val toLocation: BoardLocation = possibleToLocations(random.nextInt(possibleToLocations.size))

    if (toLocation == fromLocation) {
      new Move(MoveType.Pass, None, None)
    } else if (board.getField(toLocation).player != myColor) {
      new Move(MoveType.Attack, Some(fromLocation), Some(toLocation))
    } else {
      new Move(MoveType.Strengthen, Some(fromLocation), Some(toLocation))
    }
  }

  private def getRandomAttack(board: Board): Move = {
    var move: Move = getRandomMove(board)
    while (move.moveType != MoveType.Attack) {
      move = getRandomMove(board)
    }
    move
  }
}

object Bot {
  private def getPossibleToLocations(board: Board, fromLocation: BoardLocation): List[BoardLocation] = {
    val north = (0, -1)
    val south = (0, 1)
    val east = (1, 0)
    val west = (1, 0)
    val northWest = (-1, -1)
    val southEast = (1, 1)

    val directions = List(north, south, east, west, northWest, southEast) map {
      case (x, y) => getFirstNonEmptyInDirection(board, fromLocation, x, y)
    }

    val validDirections = directions collect {
      case Some(direction) if isValidMove(board, fromLocation, direction) => direction
    }
    fromLocation :: validDirections
  }

  private def getFirstNonEmptyInDirection(board: Board, location: BoardLocation, directionX: Int, directionY: Int): Option[BoardLocation] = {
    var x: Int = location.x
    var y: Int = location.y
    do {
      x += directionX
      y += directionY
    } while (BoardLocation.IsLegal(x, y) && board.getField(x, y).player.isEmpty)

    if (!BoardLocation.IsLegal(x, y)) {
      None
    } else {
      val newLocation: BoardLocation = new BoardLocation(x, y)
      if (newLocation == location || board.getField(newLocation).player.isEmpty) {
        None
      } else {
        Some(newLocation)
      }
    }
  }

  private def isValidMove(board: Board, from: BoardLocation, to: BoardLocation): Boolean = {
    val fromOwner = board.getField(from).player
    val toOwner = board.getField(to).player
    val fromHeight: Int = board.getField(from).height
    val toHeight: Int = board.getField(to).height
    (fromOwner != None) && (toOwner != None) && ((fromOwner == toOwner) || fromHeight >= toHeight)
  }

  private def allLegalBoardLocations(): List[BoardLocation] = {
    for {
      y <- (0 until 9).toList
      x <- (0 until 9).toList
      if BoardLocation.IsLegal(x, y)
    } yield new BoardLocation(x, y)
  }
}