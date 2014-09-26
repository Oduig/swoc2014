package com.gjos.scala.swoc.protocol

import java.util

object MoveCache {
  private val cacheLimit = 10000
  private var size = 0
  private val cacheAttack = new java.util.HashMap[BoardHash, util.ArrayList[Int]]()
  private val cacheAll = new java.util.HashMap[BoardHash, util.ArrayList[Int]]()
  private val enabled = false

  def hasKey(board: FastBoard, mustAttack: Boolean): Boolean = enabled && {
    val h = board.myHashCode
    if (mustAttack) cacheAttack.containsKey(h) else cacheAll.containsKey(h)
  }

  def get(board: FastBoard, mustAttack: Boolean): util.ArrayList[Int] = {
    val h = board.myHashCode()
    if (mustAttack) cacheAttack.get(h) else cacheAll.get(h)
  }

  def add(board: FastBoard, mustAttack: Boolean, move: util.ArrayList[Int]): Unit = if (enabled) {
    if (size >= cacheLimit) {
      if (mustAttack) cacheAttack.clear() else cacheAll.clear()
      size = 0
    }
    size += 1
    if (mustAttack) cacheAttack.put(board.myHashCode, move) else cacheAll.put(board.myHashCode, move)
  }
}
