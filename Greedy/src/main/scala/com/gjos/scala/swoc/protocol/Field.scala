package com.gjos.scala.swoc.protocol

case class Field (player: Option[Player], stone: Option[Stone], height: Int) {
  def code = (player, stone, height) match {
    case (None, None, 0) => 0
    case (Some(p), Some(s), h) if h > 0 => p.value * (h * 4 + s.value)
  }
}

object Field {
  val empty = new Field(None, None, 0)
  val blackPebble = new Field(Some(Player.Black), Some(Stone.Pebble), 1)
  val blackRock = new Field(Some(Player.Black), Some(Stone.Rock), 1)
  val blackBoulder = new Field(Some(Player.Black), Some(Stone.Boulder), 1)
  val whitePebble = new Field(Some(Player.White), Some(Stone.Pebble), 1)
  val whiteRock = new Field(Some(Player.White), Some(Stone.Rock), 1)
  val whiteBoulder = new Field(Some(Player.White), Some(Stone.Boulder), 1)

  def getOwner(fieldCode: Int): Option[Player] = fieldCode match {
    case x if x > 0 => Some(Player.White)
    case x if x < 0 => Some(Player.Black)
    case _ => None
  }

  def getStone(fieldCode: Int): Option[Stone] = Math.abs(fieldCode) % 4 match {
    case 0 => None
    case 1 => Some(Stone.Pebble)
    case 2 => Some(Stone.Rock)
    case 3 => Some(Stone.Boulder)
  }

  def getHeight(fieldCode: Int) = Math.abs(fieldCode) / 4

  def fromCode(code: Int): Field = Field(getOwner(code), getStone(code), getHeight(code))
}
