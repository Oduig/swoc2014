package com.gjos.scala.swoc.protocol

object Field {
  def player(field: Int) = if (field > 0) Some(Player.White) else if (field < 0) Some(Player.Black) else None
  def stone(field: Int) = Stone.byValue(Math.abs(field) % 4)
  def height(field: Int) = Math.abs(field) / 4

  lazy val empty = encode(null, null, 0)
  lazy val blackPebble = encode(Player.Black, Stone.Pebble, 1)
  lazy val blackRock = encode(Player.Black, Stone.Rock, 1)
  lazy val blackBoulder = encode(Player.Black, Stone.Boulder, 1)
  lazy val whitePebble = encode(Player.White, Stone.Pebble, 1)
  lazy val whiteRock = encode(Player.White, Stone.Rock, 1)
  lazy val whiteBoulder = encode(Player.White, Stone.Boulder, 1)

  def strengthened(current: Int) = (if (current > 0) 4 else -4) + current

  private def encode(player: Player, stone: Stone, height: Int): Int = {
    if (player == null || stone == null || height == 0) 0
    else player.value * (height * 4 + stone.value)
  }
}
