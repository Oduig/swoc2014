package com.gjos.scala.swoc

import com.gjos.scala.swoc.protocol._
import com.gjos.scala.swoc.protocol.InitiateRequest
import com.gjos.scala.swoc.protocol.MoveRequest
import com.gjos.scala.swoc.protocol.ProcessedMove
import com.gjos.scala.swoc.protocol.Move
import scala.language.dynamics
import com.github.pathikrit.dijon

object JsonConverters extends Dynamic {

  /**{
    "Color": <PlayerColor>
  }*/
  def createInitiateRequest(jsonMessage: String): InitiateRequest = {
    val json = dijon.parse(jsonMessage)
    val playerNum = json.Color.as[String].get.toInt
    InitiateRequest(Player.byValue(playerNum).get)
  }

  /**{
    "Board": <Board>,
    "AllowedMoves": <MoveType[]>
  }*/
  def createMoveRequest(jsonMessage: String): MoveRequest = {
    val json = dijon.parse(jsonMessage)
    val boardSetup: Seq[Seq[Int]] = for (row <- json.Board.state.toSeq) yield for (col <- row.toSeq) yield col.as[Double].get.toInt
    val allowedMoves: Seq[Int] = for (allowedMoveJson <- json.AllowedMoves.toSeq) yield allowedMoveJson.as[String].get.toInt
    MoveRequest(Board.fromInts(boardSetup), allowedMoves.toList map MoveType.byValue)
  }

  /**{
    "Player": <PlayerColor>, // player that performed the move
    "Move": <Move>, // the actual move (type, from and to)
    "Winner": <PlayerColor> // the winner of the match after this move was processed. 0 (None) when no winner yet.
  }*/
  def createProcessedMove(jsonMessage: String): ProcessedMove = {
    val json = dijon.parse(jsonMessage)
    val playerNum: Int = json.Player.as[String].get.toInt
    val moveType: Int = json.Move.Type.as[String].get.toInt
    val fromLocation = json.Move.From match {
      case moveFrom if moveFrom == None => None
      case moveFrom => Some(BoardLocation(moveFrom.X.as[Double].get.toInt, moveFrom.Y.as[Double].get.toInt))
    }
    val toLocation = json.Move.To match {
      case moveFrom if moveFrom == None => None
      case moveTo => Some(BoardLocation(moveTo.X.as[Double].get.toInt, moveTo.Y.as[Double].get.toInt))
    }
    val winnerPlayerNum: Int = json.Winner.as[String].get.toInt
    val move = Move(MoveType.byValue(moveType), fromLocation, toLocation)
    ProcessedMove(Player.byValue(playerNum).get, move, Player.byValue(winnerPlayerNum))
  }

  /**{
    "Type": <MoveType>,
    "From": <BoardLocation>
    "To": <BoardLocation>
  }*/
  def toJson(move: Move): String = {
    def boardLocationToJson(obl: Option[BoardLocation], key: String) = obl match {
      case Some(BoardLocation(x, y)) => s""""$key": { "x": $x, "y": $y }"""
      case None => ""
    }

    List(
      s""""Type": ${move.moveType.value}""",
      boardLocationToJson(move.from, "From"),
      boardLocationToJson(move.to, "To")
    ).filter(_.nonEmpty).mkString("{", ", ", "}")
  }
}
