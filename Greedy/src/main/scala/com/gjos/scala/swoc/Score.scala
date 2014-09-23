package com.gjos.scala.swoc

import com.gjos.scala.swoc.protocol.{Field, Board, Stone, Player}
import com.gjos.scala.swoc.util.ScoreCache

object Score {
  /**
   * Returns the current board's utility from a given player's perspective.
   * Rates the board by the difference between us and them,
   * where score is determined by how close we are to having a type of stones eliminated
   */
  def score(board: Board, us: Player): Int = {
    if (ScoreCache.hasKey(board)) {
      ScoreCache.get(board)
    } else {
      val them = us.opponent

      val myScore = utility(board, us)
      val theirScore = utility(board, them)
      //println(s"Me: $myScore, them: $theirScore")
      //board.dump()

      val score = if (theirScore <= 0) {
        // If we can win this turn, the rest doesn't matter.
        Int.MaxValue
      } else if (myScore <= 0) {
        // If we made a losing move, it's bad. But i
        Int.MinValue
      } else {
        myScore - theirScore
      }
      ScoreCache.add(board, score)
      score
    }
  }

  /**
   * Best primary metric seems to be the minimal type of stone,
   * aka how close we are to death.
   * Still, it's good to factor in other stones slightly,
   * because we cannot always take their most valuable stone.
   */
  def utility(b: Board, p: Player): Int = {
    val fields = b.iterator
    var pebbleValue = 0
    var rockValue = 0
    var boulderValue = 0
    while (fields.hasNext) {
      val field = fields.next()
      if (Field.player(field) == p) {
        val stone = Field.stone(field)
        val value = Math.pow(Field.height(field) * 10, 1.4).toInt
        if (stone == Stone.Pebble) {
          pebbleValue += value
        } else if (stone == Stone.Rock) {
          rockValue += value
        } else {
          boulderValue += value
        }
      }
    }

    //println(s"Pebble: $pebbleValue, Rock: $rockValue, Boulder: $boulderValue")
    val minScore = Math.min(pebbleValue, Math.min(rockValue, boulderValue))
    if (minScore <= 0) Int.MinValue
    else (100 * (Math.pow(pebbleValue, 0.4) + Math.pow(rockValue, 0.4) + Math.pow(boulderValue, 0.4))).toInt
  }
}
