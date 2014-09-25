package com.gjos.scala.swoc.protocol

case class MoveRequest(board: FastBoard, allowedMoves: Array[MoveType])