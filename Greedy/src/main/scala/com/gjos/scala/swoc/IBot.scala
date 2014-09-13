package com.gjos.scala.swoc

import com.gjos.scala.swoc.protocol.{ProcessedMove, Move, MoveRequest, InitiateRequest}

trait IBot {
  def HandleInitiate(request: InitiateRequest)

  def HandleMove(request: MoveRequest): Move

  def HandleProcessedMove(move: ProcessedMove)
}

