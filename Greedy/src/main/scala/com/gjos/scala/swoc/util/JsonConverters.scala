package com.gjos.scala.swoc.util

import com.gjos.scala.swoc.protocol._
import org.json.simple.{JSONArray, JSONObject}
import com.gjos.scala.swoc.protocol.MoveRequest
import com.gjos.scala.swoc.protocol.InitiateRequest
import com.gjos.scala.swoc.protocol.ProcessedMove
import scala.Some
import com.gjos.scala.swoc.protocol.Move

object JsonConverters {
  import JsonParser._

  /**{
    "Color": <PlayerColor>
  }*/
  def createInitiateRequest(jsonMessage: String): InitiateRequest = {
    val playerNum = parse(jsonMessage).getAs[String]("Color").toByte
    InitiateRequest(Player.byValue(playerNum).get)
  }

  /**{
    "Board": <Board>,
    "AllowedMoves": <MoveType[]>
  }*/
  def createMoveRequest(jsonMessage: String): MoveRequest = {
    val json = parse(jsonMessage)
    val boardSetup: List[List[Byte]] =
      for (row <- json.getAs[JSONObject]("Board").getList[JSONArray]("state"))
      yield row.getList[Long]() map (_.toByte)
    val allowedMoves: List[Byte] = json.getList[String]("AllowedMoves") map (_.toByte)
    MoveRequest(Board.fromBytes(boardSetup), allowedMoves.toList map MoveType.byValue)
  }

  /**{
    "Player": <PlayerColor>, // player that performed the move
    "Move": <Move>, // the actual move (type, from and to)
    "Winner": <PlayerColor> // the winner of the match after this move was processed. 0 (None) when no winner yet.
  }*/
  def createProcessedMove(jsonMessage: String): ProcessedMove = {
    val json = parse(jsonMessage)
    val playerNum = json.getAs[String]("Player").toByte
    val moveObject = json.getAs[JSONObject]("Move")
    val moveType = moveObject.getAs[String]("Type").toByte
    val fromLocation = Option(moveObject.getAs[Any]("From")) map {
      case moveFrom: JSONObject => (moveFrom.getAs[Long]("X").toByte, moveFrom.getAs[Long]("Y").toByte)
    }
    val toLocation = Option(moveObject.getAs[Any]("To")) map {
      case moveTo: JSONObject => (moveTo.getAs[Long]("X").toByte, moveTo.getAs[Long]("Y").toByte)
    }
    val winnerPlayerNum = json.getAs[String]("Winner").toByte
    val move = Move(MoveType.byValue(moveType), fromLocation, toLocation)
    ProcessedMove(Player.byValue(playerNum).get, move, Player.byValue(winnerPlayerNum))
  }

  /**{
    "Type": <MoveType>,
    "From": <BoardLocation>
    "To": <BoardLocation>
  }*/
  def toJson(move: Move): String = {
    def boardLocationToJson(obl: Option[(Byte, Byte)], key: String) = obl match {
      case Some((x, y)) => s""""$key": { "X": $x, "Y": $y }"""
      case None => ""
    }

    List(
      s""""Type": ${move.moveType.value}""",
      boardLocationToJson(move.from, "From"),
      boardLocationToJson(move.to, "To")
    ).filter(_.nonEmpty).mkString("{", ", ", "}")
  }
}
