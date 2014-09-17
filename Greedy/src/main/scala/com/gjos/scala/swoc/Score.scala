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
    val them = us.opponent

    val myScore = utility(board, us)
    val theirScore = utility(board, them)

    if (theirScore <= 0) { // If we can win this turn, the rest doesn't matter.
      Float.MaxValue
    } else if (myScore <= 0) { // If we made a losing move, it's bad. But i
      Float.MinValue
    } else {
      myScore - theirScore
    }
  }

  /**
   * Best primary metric seems to be the minimal type of stone,
   * aka how close we are to death.
   * Still, it's good to factor in other stones slightly,
   * because we cannot always take their most valuable stone.
   */
  def utility(b: Board, p: Player) = {
    val lowestFactor = 1f
    val totalFactor = .01f
    val stonesLeft = b.stonesLeft(p)
    val minScore = stonesLeft.min
    if (minScore <= 0f) Float.MinValue else minScore * lowestFactor + stonesLeft.sum * totalFactor
  }
}
