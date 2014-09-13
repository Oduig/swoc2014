package com.gjos.scala.swoc

import java.io.{BufferedReader, IOException, InputStreamReader}
import com.gjos.scala.swoc.protocol.{ProcessedMove, Move, MoveRequest, Player}

class Engine(private val bot: IBot) extends AutoCloseable {

  private final val inStreamReader = new InputStreamReader(System.in)
  private final val inReader = new BufferedReader(inStreamReader)
  private var botColor: Option[Player] = None

  def run() {
    doInitiateRequest()
    var winner = doFirstRound()
    while (winner == None) {
      winner = doNormalRound()
    }
  }

  private def doInitiateRequest() {
    val initRequest = JsonConverters.createInitiateRequest(readMessage())
    botColor = Some(initRequest.color)
    bot.handleInitiate(initRequest)
  }

  private def doFirstRound(): Option[Player] = botColor match {
    case Some(player) if player == Player.White =>
      handleMoveRequest()
      handleProcessedMove() orElse handleProcessedMove() orElse handleProcessedMove()
    case _ => handleProcessedMove()
  }

  private def doNormalRound(): Option[Player] = {
    def handleMoveAndProcess() = {
      handleMoveRequest()
      handleProcessedMove()
    }
    handleMoveAndProcess() orElse handleMoveAndProcess() orElse handleProcessedMove() orElse handleProcessedMove()
  }

  private def handleMoveRequest() {
    val moveRequest: MoveRequest = JsonConverters.createMoveRequest(readMessage())
    val move: Move = bot.handleMove(moveRequest)
    writeMessage(JsonConverters.toJson(move))
  }

  private def handleProcessedMove(): Option[Player] = {
    val processedMove: ProcessedMove = JsonConverters.createProcessedMove(readMessage())
    bot.handleProcessedMove(processedMove)
    processedMove.winner
  }

  def close() {
    inReader.close()
    inStreamReader.close()
  }

  private def readMessage() = inReader.readLine

  private def writeMessage(message: String) = System.out.println(message)
}
