package com.gjos.scala.swoc

import com.gjos.scala.swoc.protocol.{ProcessedMove2, Move2, MoveRequest2, InitiateRequest2}

trait IBot2 {
  def HandleInitiate(request: InitiateRequest2)

  def HandleMove(request: MoveRequest2): Move2

  def HandleProcessedMove(move: ProcessedMove2)
}

