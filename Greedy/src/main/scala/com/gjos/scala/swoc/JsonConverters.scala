package com.gjos.scala.swoc

import com.gjos.scala.swoc.protocol._
import com.gjos.scala.swoc.protocol.InitiateRequest2
import com.gjos.scala.swoc.protocol.MoveRequest2
import com.gjos.scala.swoc.protocol.ProcessedMove2
import com.gjos.scala.swoc.protocol.Move2
import scala.language.dynamics
import com.github.pathikrit.dijon

object JsonConverters extends Dynamic {

  /**{
    "Color": <PlayerColor>
  }*/
  def createInitiateRequest(jsonMessage: String): InitiateRequest2 = {
    val json = dijon.parse(jsonMessage)
    val playerNum = json.Color.as[String].get.toInt
    InitiateRequest2(Player2.byValue(playerNum).get)
  }

  /**{
    "Board": <Board>,
    "AllowedMoves": <MoveType[]>
  }*/
  def createMoveRequest(jsonMessage: String): MoveRequest2 = {
    val json = dijon.parse(jsonMessage)
    val boardSetup: Seq[Seq[Int]] = for (row <- json.Board.state.toSeq) yield for (col <- row.toSeq) yield col.as[Double].get.toInt
    val allowedMoves: Seq[Int] = for (allowedMoveJson <- json.AllowedMoves.toSeq) yield allowedMoveJson.as[String].get.toInt
    MoveRequest2(Board2.fromInts(boardSetup), allowedMoves.toList map MoveType2.byValue)
  }

  /**{
    "Player": <PlayerColor>, // player that performed the move
    "Move": <Move>, // the actual move (type, from and to)
    "Winner": <PlayerColor> // the winner of the match after this move was processed. 0 (None) when no winner yet.
  }*/
  def createProcessedMove(jsonMessage: String): ProcessedMove2 = {
    val json = dijon.parse(jsonMessage)
    val playerNum: Int = json.Player.as[String].get.toInt
    val moveType: Int = json.Move.Type.as[String].get.toInt
    val fromLocation = json.Move.From match {
      case moveFrom if moveFrom == None => None
      case moveFrom => Some(BoardLocation2(moveFrom.X.as[Double].get.toInt, moveFrom.Y.as[Double].get.toInt))
    }
    val toLocation = json.Move.To match {
      case moveFrom if moveFrom == None => None
      case moveTo => Some(BoardLocation2(moveTo.X.as[Double].get.toInt, moveTo.Y.as[Double].get.toInt))
    }
    val winnerPlayerNum: Int = json.Winner.as[String].get.toInt
    val move = Move2(MoveType2.byValue(moveType), fromLocation, toLocation)
    ProcessedMove2(Player2.byValue(playerNum).get, move, Player2.byValue(winnerPlayerNum))
  }

  /**{
    "Type": <MoveType>,
    "From": <BoardLocation>
    "To": <BoardLocation>
  }*/
  def toJson(move: Move2): String = {
    def boardLocationToJson(obl: Option[BoardLocation2]) = obl match {
      case Some(BoardLocation2(x, y)) => s""""From": { "x": $x, "y": $y }"""
      case None => ""
    }

    List(
      s""""Type": ${move.moveType.value}""",
      boardLocationToJson(move.from),
      boardLocationToJson(move.to)
    ).filter(_.nonEmpty).mkString("{", ", ", "}")
  }
}
