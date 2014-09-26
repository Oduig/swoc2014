package com.gjos.scala.swoc.util

import com.gjos.scala.swoc.protocol._
import org.json.simple.{JSONArray, JSONObject}
import com.gjos.scala.swoc.protocol.MoveRequest
import com.gjos.scala.swoc.protocol.Player
import com.gjos.scala.swoc.protocol.ProcessedMove
import scala.Some
import com.gjos.scala.swoc.protocol.Move

object JsonConverters {
  import JsonParser._

  /**{
    "Color": <PlayerColor>
  }*/
  def createInitiateRequest(jsonMessage: String): Player = {
    val playerNum = parse(jsonMessage).getAs[String]("Color").toInt
    if (playerNum < 0) 2 else playerNum
  }

  /**{
    "Board": <Board>,
    "AllowedMoves": <MoveType[]>
  }*/
  def createMoveRequest(jsonMessage: String): MoveRequest = {
    val json = parse(jsonMessage)
    val jsonRows = json.getAs[JSONObject]("Board").getList[JSONArray]("state").iterator()
    val board = new Array[Int](81)
    var i = 0
    while (jsonRows.hasNext) {
      val row = jsonRows.next()
      val fields = row.getList[Long]().iterator()
      while (fields.hasNext) {
        val f = fields.next().toInt
        board(i) = Field.encode(if (f == 0) 0 else if (f < 0) 2 else 1, Math.abs(f % 4), Math.abs(f / 4))
        i += 1
      }
    }
    val allowedMoves: Array[Int] = if (json.getList[String]("AllowedMoves").size == 1) Array(1) else Array(0, 1, 2)
    MoveRequest(new FastBoard(board), allowedMoves)
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
    val move = Move(moveType, fromLocation getOrElse -1, toLocation getOrElse -1)
    ProcessedMove(if (playerNum < 0) 2 else playerNum, move, winnerPlayerNum)
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
      s""""Type": ${Move.moveType(move)}""",
      boardLocationToJson(Move.from(move), "From"),
      boardLocationToJson(Move.to(move), "To")
    ).filter(_.nonEmpty).mkString("{", ", ", "}")
  }
}
