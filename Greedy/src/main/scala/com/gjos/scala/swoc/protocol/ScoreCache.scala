package com.gjos.scala.swoc.protocol

object ScoreCache {
  private val cacheLimit = 1000000
  private var size = 0
  private val cache = new java.util.HashMap[BoardHash, Int]()
  private val enabled = true

  def hasKey(board: FastBoard): Boolean = enabled && cache.containsKey(board.myHashCode)
  def get(board: FastBoard): Int = cache.get(board.myHashCode)

  def add(board: FastBoard, score: Int): Unit = if (enabled) {
    if (size >= cacheLimit) {
      cache.clear()
      size = 0
    }
    size += 1
    cache.put(board.myHashCode, score)
  }
}
