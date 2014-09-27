package com.gjos.scala.swoc.protocol

object MoveCache {
  private val cacheLimit = 10000
  private var size = 0
  private val cacheAttackWhite = new java.util.HashMap[BoardHash, Array[Int]]()
  private val cacheAllWhite = new java.util.HashMap[BoardHash, Array[Int]]()
  private val cacheAttackBlack = new java.util.HashMap[BoardHash, Array[Int]]()
  private val cacheAllBlack = new java.util.HashMap[BoardHash, Array[Int]]()
  private val enabled = true

  def hasKey(board: FastBoard, p: Player, mustAttack: Boolean): Boolean = enabled && {
    val h = board.myHashCode
    if (mustAttack) if (p == Player.White) cacheAttackWhite.containsKey(h) else cacheAttackBlack.containsKey(h)
    else if (p == Player.White) cacheAllWhite.containsKey(h) else cacheAllBlack.containsKey(h)
  }

  def get(board: FastBoard, p: Player, mustAttack: Boolean): Array[Int] = {
    val h = board.myHashCode()
    if (mustAttack) if (p == Player.White) cacheAttackWhite.get(h) else cacheAttackBlack.get(h)
    else if (p == Player.White) cacheAllWhite.get(h) else cacheAllBlack.get(h)
  }

  def add(board: FastBoard, p: Player, mustAttack: Boolean, move: Array[Int]): Unit = if (enabled) {
    if (size >= cacheLimit) {
      if (mustAttack) if (p == Player.White) cacheAttackWhite.clear() else cacheAttackBlack.clear()
      else if (p == Player.White) cacheAllWhite.clear() else cacheAllBlack.clear()
      size = 0
    }
    size += 1
    if (mustAttack) if (p == Player.White) cacheAttackWhite.put(board.myHashCode, move) else cacheAttackBlack.put(board.myHashCode, move)
    else if (p == Player.White) cacheAllWhite.put(board.myHashCode, move) else cacheAllBlack.put(board.myHashCode, move)
  }
}
