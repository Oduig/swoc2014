package com.gjos.scala.swoc

import com.gjos.scala.swoc.protocol.{ProcessedMove, Move, MoveRequest, InitiateRequest}

trait IBot {
  def handleInitiate(request: InitiateRequest)

  def handleMove(request: MoveRequest): Move

  def handleProcessedMove(move: ProcessedMove)
}

