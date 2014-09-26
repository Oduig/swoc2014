package com.gjos.scala.swoc.protocol

object Field {
  val Empty = 0

  def player(field: Field): Player = if (field > 0) Player.White else if (field < 0) Player.Black else 0
  def stone(field: Field): Stone = Math.abs(field) % 4
  def height(field: Field): Int = Math.abs(field) / 4

  // Just keep the sign and multiple of 4 of the old stone, and add it to the new one
  def strengthened(source: Field, target: Field) = (target / 4 * 4) + source

  private def encode(player: Player, stone: Stone, height: Int): Field = player * (height * 4 + stone)
}
