package com.gjos.scala.swoc.protocol

object Board {

  def applyMove(board: FastBoard, move: Move): FastBoard = {
    val newBoard = board.copy()
    Move.moveType(move) match {
      case MoveType.Pass => newBoard
      case MoveType.Attack =>
        val fromField = board.getField(Move.from(move))
        newBoard.setField(Move.from(move), Field.Empty)
        newBoard.setField(Move.to(move), fromField)
      case MoveType.Strengthen =>
        val fromField = board.getField(Move.from(move))
        val toField = board.getField(Move.to(move))
        newBoard.setField(Move.from(move), Field.Empty)
        newBoard.setField(Move.to(move), Field.strengthened(fromField, toField))
    }
    newBoard
  }

  private def totalCount(board: FastBoard, player: Player, stone: Stone): Int = board._state count { field =>
    Field.player(field) == player && Field.stone(field) == stone
  }

  def dump(board: FastBoard) {
    System.out.println("-- owners --------  -- stones --------  -- heights ----------------  -- locations --------------")
    for (y <- 0 until FastBoard.diameter) {
      for (x <- 0 until FastBoard.diameter) {
        val c = if (FastLocation.isValid(x, y)) {
          Field.player(board.getField(Location.join(x, y))) match {
            case Player.Black => 'B'
            case Player.White => 'W'
            case _ => '.'
          }
        } else ' '
        System.out.print(" " + c)
      }
      System.out.print("  ")
      for (x <- 0 until FastBoard.diameter) {
        val c = if (FastLocation.isValid(x, y)) {
          Field.stone(board.getField(Location.join(x, y))) match {
            case Stone.Pebble => 'a'
            case Stone.Rock => 'b'
            case Stone.Boulder => 'c'
            case _ => '.'
          }
        } else ' '
        System.out.print(" " + c)
      }
      System.out.print("  ")
      for (x <- 0 until FastBoard.diameter) {
        val s = if (FastLocation.isValid(x, y)) f"${Field.height(board.getField(Location.join(x, y)))}%2d" else "  "
        System.out.print(" " + s)
      }
      System.out.print("  ")
      for (x <- 0 until FastBoard.diameter) {
        val s = if (FastLocation.isValid(x, y)) Location.label(Location.join(x, y)) else "  "
        System.out.print(" " + s)
      }
      println()
    }
    println("------------------  ------------------  ---------------------------  ---------------------------")
    System.out.print("White: "
      + totalCount(board, Player.White, Stone.Pebble) + " a, "
      + totalCount(board, Player.White, Stone.Rock) + " b, "
      + totalCount(board, Player.White, Stone.Boulder) + " c")
    println(s"  Black: "
      + totalCount(board, Player.Black, Stone.Pebble) + " a, "
      + totalCount(board, Player.Black, Stone.Rock) + " b, "
      + totalCount(board, Player.Black, Stone.Boulder) + " c")
  }
}