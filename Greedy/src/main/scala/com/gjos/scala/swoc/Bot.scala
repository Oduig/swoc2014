package com.gjos.scala.swoc

import java.util.Random
import com.gjos.scala.swoc.protocol._
import com.gjos.scala.swoc.protocol.InitiateRequest
import com.gjos.scala.swoc.util.Stopwatch
import scala.concurrent.blocking
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ArrayBuffer

class Bot(private var myColor: Option[Player]) {
  private val random = new Random
  private lazy val us = myColor.get

  def handleInitiate(request: InitiateRequest) {
    myColor = Some(request.color)
  }

  def handleProcessedMove(move: ProcessedMove) {
    //println(move)
  }

  def handleMove(request: MoveRequest, singleMoveTurn: Boolean, runTime: Long = 1500): Move = request.allowedMoves match {
    case x :: Nil if x == MoveType.Attack => bestMove(request.board, !singleMoveTurn, runTime)
    case _ => bestMove(request.board, haveExtraMove = false, runTime)
  }

  def bestMove(board: Board, haveExtraMove: Boolean, runTime: Long): Move = {

    var timedOut = false
    def leastEvenScore(p: Player) = if (p == us) Float.MinValue else Float.MaxValue
    def moreEven(p: Player)(x: Float, y: Float) = if (p == us) x > y else x < y

    def minimax(b: Board, firstMoveInPath: Move, p: Player, hasExtraMove: Boolean, depth: Int, alpha: Float, beta: Float): (Move, Float, Int) = {
      if (timedOut) {
        throw new InterruptedException("Minimax interrupted due to timeout.")
      } else {
        val currentScore = b.score(us)
        // Stop at max recursion depth or when we have lost. If we have won, it's already covered.
        if (depth == 0 || currentScore == Float.MinValue) {
          (firstMoveInPath, currentScore, depth)
        } else {
          val validMoves = if (hasExtraMove) p.allValidMoves(b, attackOnly = true) else p.allValidMoves(b)
          if (validMoves.isEmpty) {
            (firstMoveInPath, leastEvenScore(p), depth)
          } else {
            val nextPlayer = if (hasExtraMove) p else p.opponent
            val nextHasExtraMove = !hasExtraMove
            val childScores = ArrayBuffer.empty[(Move, Float, Int)]
            var i: Int = 0
            var newAlpha = alpha
            var newBeta = beta
            var cutoff = false
            while (i < validMoves.size && !cutoff) {
              val validMove = validMoves(i)
              val outcome = minimax(
                b applyMove validMove,
                if (firstMoveInPath == null) validMove else firstMoveInPath,
                nextPlayer,
                nextHasExtraMove,
                depth - 1,
                newAlpha,
                newBeta
              )
              if (p == us) {
                newAlpha = Math.max(alpha, outcome._2)
              } else {
                newBeta = Math.min(beta, outcome._2)
              }
              if (newBeta < newAlpha) {
                cutoff = true
              }
              childScores += outcome
              i += 1
            }
            //println(childScores)
            // extra special bonus extension: don't take the first optimal move, take a random optimal move
            val compare = moreEven(p) _
            var evenestSoFar = leastEvenScore(p)
            var deepestSoFar: Int = depth
            var optimalMoveIndices = List[Int]()
            i = 0
            while (i < childScores.size) {
              val thisone = childScores(i)._2
              if (compare(thisone, evenestSoFar)) {
                optimalMoveIndices = List[Int](i)
                evenestSoFar = thisone
                deepestSoFar = childScores(i)._3
              } else if (thisone == evenestSoFar) {
                if (thisone == Float.MinValue) { // This is to maximize the length of the game when we are losing for sure
                  if (childScores(i)._3 < deepestSoFar) {
                    deepestSoFar = childScores(i)._3
                    optimalMoveIndices = List(i)
                  } else {}
                } else {
                  optimalMoveIndices = i :: optimalMoveIndices
                }
              }
              i += 1
            }
            val best = childScores(optimalMoveIndices.toVector(random.nextInt(optimalMoveIndices.size)))
            //println(best)
            best
          }
        }
      }
    }

    val time = Stopwatch()
    var depth = 1
    var move: Move = null
    var score: Float = 0
    Future(blocking(Thread sleep runTime)) onComplete (_ => timedOut = true)
    try {
      // We can stop if we find a game ender, and take any move.
      // Otherwise, stop on timeout.
      while (score < Float.MaxValue && score > Float.MinValue) {
        val (m, s, _) = minimax(board, null, us, haveExtraMove, depth, Float.MinValue, Float.MaxValue)
        move = m
        score = s
        time.tell(s"Explored game state with $depth move lookahead and found $m with score $s")
        depth += 1
      }
    } catch {
      case _: InterruptedException =>
    }
    move
  }
}