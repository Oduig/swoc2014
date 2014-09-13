package com.gjos.scala.swoc

import java.util.Random
import com.gjos.scala.swoc.protocol._
import scala.collection.mutable.ListBuffer
import com.gjos.scala.swoc.protocol.InitiateRequest2

class Bot2(private var myColor: Option[Player2]) extends IBot2 {
  private val random = new Random

  def HandleInitiate(request: InitiateRequest2) {
    myColor = Some(request.color)
  }

  def HandleMove(request: MoveRequest2): Move2 = request.allowedMoves match {
    case x :: Nil if x == MoveType2.Attack => GetRandomAttack(request.board)
    case _ => GetRandomMove(request.board)
  }

  def HandleProcessedMove(move: ProcessedMove2) {
  }

  private def GetRandomMove(board: Board2): Move2 = {
    val allLocations: List[BoardLocation2] = Bot2.AllLegalBoardLocations()
    import scala.collection.JavaConversions._
    val myLocations: List[BoardLocation2] = for {
      location <- allLocations
      if board.getField(location).player == myColor
    } yield location
    val fromLocation: BoardLocation2 = myLocations.get(random.nextInt(myLocations.size))
    val possibleToLocations: List[BoardLocation2] = Bot2.GetPossibleToLocations(board, fromLocation)
    val toLocation: BoardLocation2 = possibleToLocations.get(random.nextInt(possibleToLocations.size))
    if (toLocation == fromLocation) {
      new Move2(MoveType2.Pass, None, None)
    } else if (board.getField(toLocation).player != myColor) {
      new Move2(MoveType2.Attack, Some(fromLocation), Some(toLocation))
    } else {
      new Move2(MoveType2.Strengthen, Some(fromLocation), Some(toLocation))
    }
  }

  private def GetRandomAttack(board: Board2): Move2 = {
    var move: Move2 = GetRandomMove(board)
    while (move.moveType != MoveType2.Attack) {
      move = GetRandomMove(board)
    }
    move
  }
}

object Bot2 {
  private def GetPossibleToLocations(board: Board2, fromLocation: BoardLocation2): List[BoardLocation2] = {
    val possibleToLocations: ListBuffer[BoardLocation2] = ListBuffer[BoardLocation2]()
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

  private def GetFirstNonEmptyInDirection(board: Board2, location: BoardLocation2, directionX: Int, directionY: Int): Option[BoardLocation2] = {
    var x: Int = location.x
    var y: Int = location.y
    do {
      x += directionX
      y += directionY
    } while (BoardLocation2.IsLegal(x, y) && board.getField(x, y).player == None)
    if (!BoardLocation2.IsLegal(x, y)) {
      None
    } else {
      val newLocation: BoardLocation2 = new BoardLocation2(x, y)
      if ((newLocation == location) || (board.getField(newLocation).player == None)) {
        None
      } else {
        Some(newLocation)
      }
    }
  }

  private def IsValidMove(board: Board2, from: BoardLocation2, to: BoardLocation2): Boolean = {
    val fromOwner = board.getField(from).player
    val toOwner = board.getField(to).player
    val fromHeight: Int = board.getField(from).height
    val toHeight: Int = board.getField(to).height
    (fromOwner != None) && (toOwner != None) && ((fromOwner == toOwner) || fromHeight >= toHeight)
  }

  private def AllLegalBoardLocations(): List[BoardLocation2] = {
    for {
      y <- (0 until 9).toList
      x <- (0 until 9).toList
      if BoardLocation2.IsLegal(x, y)
    } yield new BoardLocation2(x, y)
  }
}