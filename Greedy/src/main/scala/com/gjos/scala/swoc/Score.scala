package com.gjos.scala.swoc

import com.gjos.scala.swoc.protocol.{Board, Stone, Player}

object Score {
  /**
   * Returns the current board's utility from a given player's perspective.
   * Rates the board by the difference between us and them,
   * where score is determined by how close we are to having a type of stones eliminated
   * todo: detect winning move (2 turns)
   * todo: detect if opponent has a winning move (1 turn)
   * todo: detect if opponent has a winning move (2 turns)
   * todo: clever lategame
   */
  def score(board: Board, us: Player): Float = {
    val them = if (us == Player.Black) Player.White else Player.Black

    val myScore = utility(board, us)
    val theirScore = utility(board, them)

    if (theirScore <= 0) {
      Float.MaxValue
    } else if (myScore <= 0) {
      Float.MinValue
    } else {
      myScore - theirScore
    }
  }

  def utility(b: Board, p: Player) = b.stonesLeft(p).min
}
