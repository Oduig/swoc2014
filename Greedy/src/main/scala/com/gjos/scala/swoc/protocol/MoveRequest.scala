package com.gjos.scala.swoc.protocol

case class MoveRequest(board: Board, allowedMoves: List[MoveType])