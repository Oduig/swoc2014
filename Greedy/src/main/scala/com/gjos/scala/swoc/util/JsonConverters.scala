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
    val playerNum = parse(jsonMessage).getAs[String]("Color").toInt
    InitiateRequest(Player.byValue(playerNum))
  }

  /**{
    "Board": <Board>,
    "AllowedMoves": <MoveType[]>
  }*/
  def createMoveRequest(jsonMessage: String): MoveRequest = {
    val json = parse(jsonMessage)
    val boardSetup: List[List[Field]] =
      for (row <- json.getAs[JSONObject]("Board").getList[JSONArray]("state"))
      yield row.getList[Long]() map (_.toInt)
    val allowedMoves: List[Int] = json.getList[String]("AllowedMoves") map (_.toInt)
    MoveRequest(Board.fromInts(boardSetup), allowedMoves.toList map MoveType.byValue)
  }

  /**{
    "Player": <PlayerColor>, // player that performed the move
    "Move": <Move>, // the actual move (type, from and to)
    "Winner": <PlayerColor> // the winner of the match after this move was processed. 0 (None) when no winner yet.
  }*/
  def createProcessedMove(jsonMessage: String): ProcessedMove = {
    val json = parse(jsonMessage)
    val playerNum = json.getAs[String]("Player").toInt
    val moveObject = json.getAs[JSONObject]("Move")
    val moveType = moveObject.getAs[String]("Type").toInt
    val fromLocation = Option(moveObject.getAs[Any]("From")) map {
      case moveFrom: JSONObject => Location.join(moveFrom.getAs[Long]("X").toInt, moveFrom.getAs[Long]("Y").toInt)
    }
    val toLocation = Option(moveObject.getAs[Any]("To")) map {
      case moveTo: JSONObject => Location.join(moveTo.getAs[Long]("X").toInt, moveTo.getAs[Long]("Y").toInt)
    }
    val winnerPlayerNum = json.getAs[String]("Winner").toInt
    val move = Move(MoveType.byValue(moveType), fromLocation getOrElse -1, toLocation getOrElse -1)
    ProcessedMove(Player.byValue(playerNum), move, Player.byValue(winnerPlayerNum))
  }

  /**{
    "Type": <MoveType>,
    "From": <BoardLocation>
    "To": <BoardLocation>
  }*/
  def toJson(move: Move): String = {
    def boardLocationToJson(bl: Location, key: String) =
      if (bl >= 0) {
        val xy = Location.split(bl)
        s""""$key": { "X": ${xy._1}, "Y": ${xy._2} }"""
      } else ""

    List(
      s""""Type": ${Move.moveType(move).value}""",
      boardLocationToJson(Move.from(move), "From"),
      boardLocationToJson(Move.to(move), "To")
    ).filter(_.nonEmpty).mkString("{", ", ", "}")
  }
}
