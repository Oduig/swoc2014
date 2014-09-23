package com.gjos.scala.swoc.util

import com.gjos.scala.swoc.protocol.Board

object ScoreCache {
  private val cacheLimit = 1000000
  private var size = 0
  private val cache = new java.util.HashMap[Int, Int]()
  private val enabled = true

  def hasKey(board: Board): Boolean = enabled && cache.containsKey(board.myHashCode)
  def get(board: Board): Int = cache.get(board.myHashCode)

  def add(board: Board, score: Int): Unit = if (enabled) {
    if (size >= cacheLimit) {
      cache.clear()
      size = 0
    }
    size += 1
    cache.put(board.myHashCode, score)
  }
}
