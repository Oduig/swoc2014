package com.gjos.scala.swoc

import java.io.{BufferedReader, IOException, InputStreamReader}
import com.gjos.scala.swoc.protocol.{ProcessedMove, Move, MoveRequest, Player}

class Engine(private val bot: IBot) extends AutoCloseable {

  private final val inStreamReader = new InputStreamReader(System.in)
  private final val inReader = new BufferedReader(inStreamReader)
  private var botColor: Option[Player] = None

  def run() {
    try {
      DoInitiateRequest()
      var winner = DoFirstRound()
      while (winner == None) {
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
    botColor = Some(initRequest.color)
    bot.HandleInitiate(initRequest)
  }

  private def DoFirstRound(): Option[Player] = botColor match {
    case Some(player) if player == Player.White =>
      HandleMoveRequest()
      val move1 = HandleProcessedMove()
      if (move1 != None) {
        move1
      } else {
        val move2 = HandleProcessedMove()
        if (move2 != None) {
          move2
        } else {
          HandleProcessedMove()
        }
      }
    case _ => HandleProcessedMove()
  }

  private def DoNormalRound(): Option[Player] = {
    var winner: Option[Player] = None
    HandleMoveRequest()
    winner = HandleProcessedMove()
    if (winner != None) {
      return winner
    }
    HandleMoveRequest()
    winner = HandleProcessedMove()
    if (winner != None) {
      return winner
    }
    winner = HandleProcessedMove()
    if (winner != None) {
      return winner
    }
    winner = HandleProcessedMove()
    winner
  }

  private def HandleMoveRequest() {
    val moveRequest: MoveRequest = JsonConverters.createMoveRequest(readMessage())
    val move: Move = bot.HandleMove(moveRequest)
    writeMessage(JsonConverters.toJson(move))
  }

  private def HandleProcessedMove(): Option[Player] = {
    val processedMove: ProcessedMove = JsonConverters.createProcessedMove(readMessage())
    bot.HandleProcessedMove(processedMove)
    processedMove.winner
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

