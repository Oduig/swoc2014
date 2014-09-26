package com.gjos.scala.swoc.protocol

object Field {
  FastField.init()
  val Empty = 0

  def player(field: Field): Player = FastField.player(field)
  def stone(field: Field): Stone = FastField.stone(field)
  def height(field: Field): Int = FastField.height(field)

  def strengthened(source: Field, target: Field) = (target % 100) + source

  def encode(player: Player, stone: Stone, height: Int): Field = if (height == 0) Empty else FastField.encode(player)(stone)(height)
}
