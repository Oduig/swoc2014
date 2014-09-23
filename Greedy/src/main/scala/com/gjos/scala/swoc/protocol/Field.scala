package com.gjos.scala.swoc.protocol

object Field {
  def player(field: Byte) = if (field > 0) Some(Player.White) else if (field < 0) Some(Player.Black) else None
  def stone(field: Byte) = Stone.byValue((Math.abs(field) % 4).toByte)
  def height(field: Byte) = (Math.abs(field) / 4).toByte

  lazy val empty = encode(null, null, 0)
  lazy val blackPebble = encode(Player.Black, Stone.Pebble, 1)
  lazy val blackRock = encode(Player.Black, Stone.Rock, 1)
  lazy val blackBoulder = encode(Player.Black, Stone.Boulder, 1)
  lazy val whitePebble = encode(Player.White, Stone.Pebble, 1)
  lazy val whiteRock = encode(Player.White, Stone.Rock, 1)
  lazy val whiteBoulder = encode(Player.White, Stone.Boulder, 1)

  def strengthened(current: Byte) = ((if (current > 0) 4 else -4) + current).toByte

  private def encode(player: Player, stone: Stone, height: Byte): Byte = {
    if (player == null || stone == null || height == 0) 0
    else (player.value * (height * 4 + stone.value)).toByte
  }
}
