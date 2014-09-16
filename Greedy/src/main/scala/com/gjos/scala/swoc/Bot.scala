package com.gjos.scala.swoc

import java.util.Random
import com.gjos.scala.swoc.protocol._
import com.gjos.scala.swoc.protocol.InitiateRequest
import com.gjos.scala.swoc.util.Stopwatch
import scala.concurrent.blocking
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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
    def moreEven(p: Player)(x: Float, y: Float) = if (p == us) x > y else y > x

    def minimax(b: Board, firstMoveInPath: Move, p: Player, hasExtraMove: Boolean, depth: Int): (Move, Float) = {
      if (timedOut) {
        throw new InterruptedException("Minimax interrupted due to timeout.")
      } else if (depth == 0) {
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
              nextHasExtraMove,
              depth - 1
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

    //val time = new Stopwatch(outputEnabled = true)
    var depth = 1
    var move: Move = null
    Future(blocking(Thread sleep runTime)) onComplete (_ => timedOut = true)
    try {
      while (true) {
        move = minimax(board, null, us, haveExtraMove, depth)._1
        //time.tell(s"Explored game state with $depth move lookahead.")
        depth += 1
      }
    } catch {
      case _: InterruptedException =>
    }
    move
  }
}