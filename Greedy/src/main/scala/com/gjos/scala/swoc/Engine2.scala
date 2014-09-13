package com.gjos.scala.swoc

import java.io.{BufferedReader, IOException, InputStreamReader}
import com.gjos.scala.swoc.protocol.{ProcessedMove2, Move2, MoveRequest2, Player2}

class Engine2(private val bot: IBot2) extends AutoCloseable {

  private final val inStreamReader = new InputStreamReader(System.in)
  private final val inReader = new BufferedReader(inStreamReader)
  private var botColor: Option[Player2] = None

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

  private def DoFirstRound(): Option[Player2] = botColor match {
    case Some(player) if player == Player2.White =>
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

  private def DoNormalRound(): Option[Player2] = {
    var winner: Option[Player2] = None
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
    val moveRequest: MoveRequest2 = JsonConverters.createMoveRequest(readMessage())
    val move: Move2 = bot.HandleMove(moveRequest)
    writeMessage(JsonConverters.toJson(move))
  }

  private def HandleProcessedMove(): Option[Player2] = {
    val processedMove: ProcessedMove2 = JsonConverters.createProcessedMove(readMessage())
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

