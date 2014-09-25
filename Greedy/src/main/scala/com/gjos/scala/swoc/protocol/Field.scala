package com.gjos.scala.swoc.protocol

object Field {
  def player(field: Field) = if (field > 0) Player.White else if (field < 0) Player.Black else null
  def stone(field: Field) = Stone.byValue(Math.abs(field) % 4)
  def height(field: Field) = Math.abs(field) / 4

  lazy val empty = encode(null, null, 0)
  lazy val blackPebble = encode(Player.Black, Stone.Pebble, 1)
  lazy val blackRock = encode(Player.Black, Stone.Rock, 1)
  lazy val blackBoulder = encode(Player.Black, Stone.Boulder, 1)
  lazy val whitePebble = encode(Player.White, Stone.Pebble, 1)
  lazy val whiteRock = encode(Player.White, Stone.Rock, 1)
  lazy val whiteBoulder = encode(Player.White, Stone.Boulder, 1)

  // Just keep the sign and multiple of 4 of the old stone, and add it to the new one
  def strengthened(source: Field, target: Field) = (target / 4 * 4) + source

  private def encode(player: Player, stone: Stone, height: Int): Field = {
    if (player == null || stone == null || height == 0) 0
    else player.value * (height * 4 + stone.value)
  }
}
