package com.gjos.scala.swoc.protocol

object ScoreCache {
  private val cacheLimit = 1000000
  private var size = 0
  private val cache = new java.util.HashMap[BoardHash, Score]()
  private val enabled = true

  def hasKey(board: Board): Boolean = enabled && cache.containsKey(board.myHashCode)
  def get(board: Board): Score = cache.get(board.myHashCode)

  def add(board: Board, score: Score): Unit = if (enabled) {
    if (size >= cacheLimit) {
      cache.clear()
      size = 0
    }
    size += 1
    cache.put(board.myHashCode, score)
  }
}
