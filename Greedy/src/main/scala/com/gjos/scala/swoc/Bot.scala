package com.gjos.scala.swoc

import java.util.Random
import com.gjos.scala.swoc.protocol._
import com.gjos.scala.swoc.protocol.InitiateRequest
import com.gjos.scala.swoc.util.Stopwatch

class Bot(private var myColor: Option[Player]) {
  private val random = new Random
  private lazy val us = myColor.get

  def handleInitiate(request: InitiateRequest) {
    myColor = Some(request.color)
  }

  def handleMove(request: MoveRequest, singleMoveTurn: Boolean): Move = request.allowedMoves match {
    case x :: Nil if x == MoveType.Attack => bestMove(request.board, !singleMoveTurn)
    case _ => bestMove(request.board, false)
  }

  def handleProcessedMove(move: ProcessedMove) {
    //println(move)
  }

  def bestMove(board: Board, haveExtraMove: Boolean, runTime: Long = 1000): Move = {
    val time = new Stopwatch()

    def leastEvenScore(p: Player) = if (p == us) Float.MinValue else Float.MaxValue
    def moreEven(p: Player)(x: Float, y: Float) = if (p == us) x > y else y > x

    // Technically it is better to keep scoring children, but we have to stop when the time is up.
    // Hence we need an algorithm to explore the best states in as many directions as possible.
    // Keywords: BFS, minimax, alpha-beta pruning
    def minimax(b: Board, firstMoveInPath: Move, p: Player, hasExtraMove: Boolean): (Move, Float) = {
      if (time.sinceStart > runTime) {
        firstMoveInPath -> b.score(us)
      } else {
        val validMoves = p.allValidMoves(b) filter (!hasExtraMove || _.moveType == MoveType.Attack)
        if (validMoves.isEmpty) {
          firstMoveInPath -> (if (p == us) Float.MinValue else Float.MaxValue)
        } else {
          val nextPlayer = if (hasExtraMove) p else p.opponent
          val nextHasExtraMove = !hasExtraMove
          val childScores = validMoves map { validMove => minimax(
              b applyMove validMove,
              if (firstMoveInPath == null) validMove else firstMoveInPath,
              nextPlayer,
              nextHasExtraMove
            )
          }
          // extra special bonus extension: don't take the first optimal move, but take a random optimal move
          val compare = moreEven(p) _
          var evenestSoFar = leastEvenScore(p)
          var optimalMoveIndices = Vector[Int]()
          var i = 0
          while (i < childScores.size) {
            val thisone = childScores(i)._2
            if (compare(thisone, evenestSoFar)) {
              optimalMoveIndices = Vector[Int](i)
              evenestSoFar = thisone
            } else if (thisone == evenestSoFar) {
              optimalMoveIndices = optimalMoveIndices :+ i
            }
            i += 1
          }
          childScores(optimalMoveIndices(random.nextInt(optimalMoveIndices.size)))
        }
      }
    }

    minimax(board, null, us, haveExtraMove)._1
  }
}