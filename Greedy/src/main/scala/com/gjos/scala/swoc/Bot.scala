package com.gjos.scala.swoc

import java.util.Random
import com.gjos.scala.swoc.protocol._
import scala.collection.mutable.ListBuffer
import com.gjos.scala.swoc.protocol.InitiateRequest

class Bot(private var myColor: Option[Player]) extends IBot {
  private val random = new Random

  def HandleInitiate(request: InitiateRequest) {
    myColor = Some(request.color)
  }

  def HandleMove(request: MoveRequest): Move = request.allowedMoves match {
    case x :: Nil if x == MoveType.Attack => GetRandomAttack(request.board)
    case _ => GetRandomMove(request.board)
  }

  def HandleProcessedMove(move: ProcessedMove) {
  }

  private def GetRandomMove(board: Board): Move = {
    val allLocations: List[BoardLocation] = Bot.AllLegalBoardLocations()
    import scala.collection.JavaConversions._
    val myLocations: List[BoardLocation] = for {
      location <- allLocations
      if board.getField(location).player == myColor
    } yield location
    val fromLocation: BoardLocation = myLocations.get(random.nextInt(myLocations.size))
    val possibleToLocations: List[BoardLocation] = Bot.GetPossibleToLocations(board, fromLocation)
    val toLocation: BoardLocation = possibleToLocations.get(random.nextInt(possibleToLocations.size))
    if (toLocation == fromLocation) {
      new Move(MoveType.Pass, None, None)
    } else if (board.getField(toLocation).player != myColor) {
      new Move(MoveType.Attack, Some(fromLocation), Some(toLocation))
    } else {
      new Move(MoveType.Strengthen, Some(fromLocation), Some(toLocation))
    }
  }

  private def GetRandomAttack(board: Board): Move = {
    var move: Move = GetRandomMove(board)
    while (move.moveType != MoveType.Attack) {
      move = GetRandomMove(board)
    }
    move
  }
}

object Bot {
  private def GetPossibleToLocations(board: Board, fromLocation: BoardLocation): List[BoardLocation] = {
    val possibleToLocations: ListBuffer[BoardLocation] = ListBuffer[BoardLocation]()
    possibleToLocations.append(fromLocation)
    val north = GetFirstNonEmptyInDirection(board, fromLocation, 0, -1)
    if (north.nonEmpty && IsValidMove(board, fromLocation, north.get)) possibleToLocations.append(north.get)
    val south = GetFirstNonEmptyInDirection(board, fromLocation, 0, 1)
    if (south.nonEmpty && IsValidMove(board, fromLocation, south.get)) possibleToLocations.append(south.get)
    val east = GetFirstNonEmptyInDirection(board, fromLocation, 1, 0)
    if (east.nonEmpty && IsValidMove(board, fromLocation, east.get)) possibleToLocations.append(east.get)
    val west = GetFirstNonEmptyInDirection(board, fromLocation, -1, 0)
    if (west.nonEmpty && IsValidMove(board, fromLocation, west.get)) possibleToLocations.append(west.get)
    val northWest = GetFirstNonEmptyInDirection(board, fromLocation, -1, -1)
    if (northWest.nonEmpty && IsValidMove(board, fromLocation, northWest.get)) possibleToLocations.append(northWest.get)
    val southEast = GetFirstNonEmptyInDirection(board, fromLocation, 1, 1)
    if (southEast.nonEmpty && IsValidMove(board, fromLocation, southEast.get)) possibleToLocations.append(southEast.get)
    possibleToLocations.toList
  }

  private def GetFirstNonEmptyInDirection(board: Board, location: BoardLocation, directionX: Int, directionY: Int): Option[BoardLocation] = {
    var x: Int = location.x
    var y: Int = location.y
    do {
      x += directionX
      y += directionY
    } while (BoardLocation.IsLegal(x, y) && board.getField(x, y).player == None)
    if (!BoardLocation.IsLegal(x, y)) {
      None
    } else {
      val newLocation: BoardLocation = new BoardLocation(x, y)
      if ((newLocation == location) || (board.getField(newLocation).player == None)) {
        None
      } else {
        Some(newLocation)
      }
    }
  }

  private def IsValidMove(board: Board, from: BoardLocation, to: BoardLocation): Boolean = {
    val fromOwner = board.getField(from).player
    val toOwner = board.getField(to).player
    val fromHeight: Int = board.getField(from).height
    val toHeight: Int = board.getField(to).height
    (fromOwner != None) && (toOwner != None) && ((fromOwner == toOwner) || fromHeight >= toHeight)
  }

  private def AllLegalBoardLocations(): List[BoardLocation] = {
    for {
      y <- (0 until 9).toList
      x <- (0 until 9).toList
      if BoardLocation.IsLegal(x, y)
    } yield new BoardLocation(x, y)
  }
}