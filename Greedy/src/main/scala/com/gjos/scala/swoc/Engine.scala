package com.gjos.scala.swoc

import java.io.{BufferedReader, IOException, InputStreamReader}
import gos.bot.protocol.Player
import gos.bot.InvalidMessageException
import gos.bot.protocol.MoveRequest
import gos.bot.protocol.Move
import gos.bot.protocol.ProcessedMove

class Engine2(private val bot: IBot2) extends AutoCloseable {

  private final val inStreamReader = new InputStreamReader(System.in)
  private final val inReader = new BufferedReader(inStreamReader)
  private var botColor: Option[Player] = None

  def run() {
    try {
      DoInitiateRequest()
      var winner: Player = DoFirstRound()
      while (winner eq Player.None) {
        winner = DoNormalRound()
      }
    } catch {
      case ex: Exception =>
        System.err.println("Exception. Bailing out.")
        ex.printStackTrace(System.err)
    }
  }

  private def DoInitiateRequest() {
    val initRequest = JsonConverters.createInitiateRequest(readMessage())
    if (initRequest == null) {
      throw new InvalidMessageException("Unexpected message received. Expected InitiateRequest.")
    }
    botColor = Some(initRequest.color)
    bot.HandleInitiate(initRequest)
  }

  private def DoFirstRound(): Player = botColor match {
    case Some(player) if player eq Player.White =>
      HandleMoveRequest()
      val move1 = HandleProcessedMove()
      if (move1 ne Player.None) {
        move1
      } else {
        val move2 = HandleProcessedMove()
        if (move2 ne Player.None) {
          move2
        } else {
          HandleProcessedMove()
        }
      }
    case _ => HandleProcessedMove()
  }

  private def DoNormalRound(): Player = {
    var winner: Player = null
    HandleMoveRequest()
    winner = HandleProcessedMove()
    if (winner ne Player.None) {
      return winner
    }
    HandleMoveRequest()
    winner = HandleProcessedMove()
    if (winner ne Player.None) {
      return winner
    }
    winner = HandleProcessedMove()
    if (winner ne Player.None) {
      return winner
    }
    winner = HandleProcessedMove()
    winner
  }

  private def HandleMoveRequest() {
    val moveRequest: MoveRequest = JsonConverters.createMoveRequest(readMessage())
    if (moveRequest == null) {
      throw new InvalidMessageException("Unexpected message received. Expected MoveRequest.")
    }
    val move: Move = bot.HandleMove(moveRequest)
    writeMessage(JsonConverters.toJson(move))
  }

  private def HandleProcessedMove(): Player = {
    val processedMove: ProcessedMove = JsonConverters.createProcessedMove(readMessage())
    if (processedMove == null) {
      throw new InvalidMessageException("Unexpected message received. Expected ProcessedMove.")
    }
    bot.HandleProcessedMove(processedMove)
    processedMove.Winner
  }

  def close() {
    try {
      inReader.close()
      inStreamReader.close()
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

  private def readMessage() = {
    val messageStr = inReader.readLine
    if (messageStr == null || messageStr.isEmpty)
      throw new IllegalArgumentException("Received message was missing or empty.")
    messageStr
  }

  private def writeMessage(message: String) = System.out.println(message)
}

