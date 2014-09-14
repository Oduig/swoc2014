package com.gjos.scala.swoc

import java.util.Random
import com.gjos.scala.swoc.protocol._
import com.gjos.scala.swoc.protocol.InitiateRequest
import com.gjos.scala.swoc.util.Stopwatch

class Bot(private var myColor: Option[Player]) {
  private val random = new Random

  def handleInitiate(request: InitiateRequest) {
    myColor = Some(request.color)
  }

  def handleMove(request: MoveRequest): Move = request.allowedMoves match {
    case x :: Nil if x == MoveType.Attack => getAttack(request.board)
    case _ => getMove(request.board)
  }

  def handleProcessedMove(move: ProcessedMove) {
    //println(move)
  }

  private def getMove(board: Board): Move = {
    Stopwatch.tell("Finding moves...")
    val allMoves = Bot.allValidMoves(board, myColor)
    Stopwatch.tell("Picking move...")
    val move = pickMove(board, allMoves)
    Stopwatch.tell("Moving.")
    move
  }

  private def getAttack(board: Board): Move = {
    Stopwatch.tell("Finding attack...")
    val allAttacks = Bot.allValidMoves(board, myColor) filter (_.moveType == MoveType.Attack)
    Stopwatch.tell("Picking move...")
    val move = pickMove(board: Board, allAttacks)
    Stopwatch.tell("Moving.")
    move
  }

  private def pickMove(board: Board, moves: Vector[Move]) = {
    val moveByScore: Vector[(Float, Move)] = for {
      move <- moves
      score = board.applyMove(move).score(myColor.get)
    } yield score -> move
    val maxScore = moveByScore.map(_._1).max
    val bestMoves: Vector[Move] = moveByScore collect {
      case (score, move) if score == maxScore => move
    }
    bestMoves(random.nextInt(bestMoves.size))
  }
}

object Bot {
  def getPossibleToLocations(board: Board, fromLocation: BoardLocation): List[BoardLocation] = {
    val targets = Direction.allDirections.map(getFirstNonEmptyInDirection(board, fromLocation, _))

    val validDirections = targets collect {
      case Some(target) if isValidMove(board, fromLocation, target) => target
    }
    fromLocation :: validDirections
  }

  def getFirstNonEmptyInDirection(board: Board, location: BoardLocation, direction: Direction): Option[BoardLocation] = {
    var x: Int = location.x
    var y: Int = location.y
    do {
      x += direction.x
      y += direction.y
    } while (BoardLocation.IsValid(x, y) && board.getField(x, y).player.isEmpty)

    if (!BoardLocation.IsValid(x, y)) {
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

  def isValidMove(board: Board, from: BoardLocation, to: BoardLocation): Boolean = {
    val fromOwner = board.getField(from).player
    val toOwner = board.getField(to).player
    val fromHeight: Int = board.getField(from).height
    val toHeight: Int = board.getField(to).height
    (fromOwner != None) && (toOwner != None) && ((fromOwner == toOwner) || fromHeight >= toHeight)
  }

  def allValidMoves(board: Board, us: Option[Player]): Vector[Move] = {
    for {
      fromLocation <- BoardLocation.allValidBoardLocations
      fromField = board.getField(fromLocation)
      if fromField.player == us
      toLocation <- Bot.getPossibleToLocations(board, fromLocation)
      toField = board.getField(toLocation)
      if fromField.height >= toField.height || toField.player == us
    } yield createMove(board, us, fromLocation, toLocation)
  }

  private def createMove(board: Board, us: Option[Player], fromLocation: BoardLocation, toLocation: BoardLocation) = {
    if (toLocation == fromLocation) {
      new Move(MoveType.Pass, None, None)
    } else if (board.getField(toLocation).player != us) {
      new Move(MoveType.Attack, Some(fromLocation), Some(toLocation))
    } else {
      new Move(MoveType.Strengthen, Some(fromLocation), Some(toLocation))
    }
  }
}