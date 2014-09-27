package com.gjos.scala.swoc

import com.gjos.scala.swoc.protocol._
import com.gjos.scala.swoc.util.JsonConverters
import com.gjos.scala.swoc.protocol.MoveRequest
import com.gjos.scala.swoc.protocol.ProcessedMove
import scala.Some

class Engine(private val bot: Bot, private val ioManager: IOManager) {

  private var botColor: Option[Player] = None

  def run() {
    doInitiateRequest()
    var winner = doFirstRound()
    while (winner == None) {
      winner = doNormalRound()
    }
  }

  private def doInitiateRequest() {
    val player = JsonConverters.createInitiateRequest(ioManager.readLine())
    botColor = Some(player)
    bot.handleInitiate(player)
  }

  private def doFirstRound(): Option[Player] = botColor match {
    case Some(Player.White) =>
      handleMoveRequest(singleMoveTurn = true)
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

  private def handleMoveRequest(singleMoveTurn: Boolean = false) {
    val moveRequest: MoveRequest = JsonConverters.createMoveRequest(ioManager.readLine())
    val move = bot.handleMove(moveRequest, singleMoveTurn)
    val out = if (move < 0) {
      System.err.println("Valar morghulis.")
      "If I die, what's the point?"
    } else {
      System.err.println("I chose move " + Move.toString(move))
      JsonConverters.toJson(move)
    }
    ioManager.writeLine(out)
  }

  private def handleProcessedMove(): Option[Player] = {
    val processedMove: ProcessedMove = JsonConverters.createProcessedMove(ioManager.readLine())
    bot.handleProcessedMove(processedMove)
    if (processedMove.winner == 0) None else Some(processedMove.winner)
  }
}

