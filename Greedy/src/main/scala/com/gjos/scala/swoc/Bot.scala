package com.gjos.scala.swoc

import java.util.Random
import com.gjos.scala.swoc.protocol._
import com.gjos.scala.swoc.protocol.InitiateRequest
import scala.concurrent.blocking
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ArrayBuffer

class Bot(private var myColor: Option[Player], private val verbose: Boolean = false) {
  private val random = new Random
  private lazy val us = myColor.get

  def handleInitiate(request: InitiateRequest) {
    myColor = Some(request.color)
  }

  def handleProcessedMove(move: ProcessedMove) {
    //println(move)
  }

  def handleMove(request: MoveRequest, singleMoveTurn: Boolean, runTime: Long = 1750): Move = request.allowedMoves match {
    case x :: Nil if x == MoveType.Attack => bestMove(request.board, mustAttack = true, singleMoveTurn, runTime)
    case _ => bestMove(request.board, mustAttack = false, singleMoveTurn = false, runTime)
  }

  def bestMove(board: Board, mustAttack: Boolean, singleMoveTurn: Boolean, runTime: Long): Move = {

    var timedOut = false
    def leastEvenScore(p: Player) = if (p == us) Int.MinValue else Int.MaxValue
    def moreEven(p: Player)(x: Int, y: Int) = if (p == us) x > y else x < y

    // Returns Move, score, longest guaranteed path length for loss
    def minimax(b: Board, firstMoveInPath: Move, p: Player, attackOnly: Boolean, hasExtraMove: Boolean, depth: Int, alpha: Int, beta: Int): (Move, Int, Int) = {
      if (timedOut) {
        throw new InterruptedException("Minimax interrupted due to timeout.")
      } else {
        val currentScore = b.score(us)
        // Stop at max recursion depth or when we have lost. If we have won, it's already covered by iterative deepening.
        if (depth == 0 || currentScore == Int.MinValue) {
          (firstMoveInPath, currentScore, 0)
        } else {
          val validMoves = p.allValidMoves(b, attackOnly)
          if (validMoves.isEmpty) {
            (firstMoveInPath, leastEvenScore(p), 0)
          } else {
            val nextPlayer = if (hasExtraMove) p else p.opponent
            val nextHasExtraMove = !hasExtraMove
            val childScores = ArrayBuffer.empty[(Move, Int, Int)]
            var i: Int = 0
            var newAlpha = alpha
            var newBeta = beta
            var cutoff = false
            while (i < validMoves.size && !cutoff) {
              if (timedOut) throw new InterruptedException("Minimax interrupted due to timeout.")
              val validMove = validMoves(i)
              //if (firstMoveInPath != null && firstMoveInPath.to == (BoardLocation fromLabel "E9"))
                //println("--" * (8-depth) + validMove) //FIXME
              val outcome = minimax(
                b applyMove validMove,
                if (firstMoveInPath == null) validMove else firstMoveInPath,
                nextPlayer,
                nextHasExtraMove,
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
            var bestLossPathLengthSoFar: Int = leastEvenScore(p)
            var optimalMoveIndices = List[Int]()
            i = 0
            while (i < childScores.size) {
              if (timedOut) throw new InterruptedException("Minimax interrupted due to timeout.")
              val thisone = childScores(i)._2
              if (compare(thisone, evenestSoFar)) {
                optimalMoveIndices = List[Int](i)
                evenestSoFar = thisone
                bestLossPathLengthSoFar = childScores(i)._3
              } else if (thisone == evenestSoFar) {
                if (thisone == Int.MinValue) { // This is to maximize the length of the game when we are losing for sure
                  if (compare(childScores(i)._3, bestLossPathLengthSoFar)) {
                    bestLossPathLengthSoFar = childScores(i)._3
                    optimalMoveIndices = List(i)
                  } else {}
                } else {
                  optimalMoveIndices = i :: optimalMoveIndices
                }
              }
              i += 1
            }
            val (move, score, pathLength) = childScores(optimalMoveIndices.toVector(random.nextInt(optimalMoveIndices.size)))
            (move, score, pathLength + 1)
          }
        }
      }
    }

    var depth = 1
    var move: Move = null
    var score: Int = 0
    Future(blocking(Thread sleep runTime)) onComplete (_ => timedOut = true)
    try {
      // We can stop if we find a game ender, and take any move.
      // Otherwise, stop on timeout.
      while (score < Int.MaxValue && score > Int.MinValue && !timedOut) {
        val (m, s, _) = minimax(board, null, us, mustAttack, !singleMoveTurn && mustAttack, depth, Int.MinValue, Int.MaxValue)
        move = m
        score = s
        if (verbose) com.gjos.scala.swoc.util.Stopwatch().tell(s"Explored game state with $depth move lookahead and found $m with score $s")
        depth += 1
      }
    } catch {
      case _: InterruptedException =>
    }
    move
  }
}