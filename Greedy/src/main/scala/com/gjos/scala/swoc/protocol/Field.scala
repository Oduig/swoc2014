package com.gjos.scala.swoc.protocol

case class Field(var player: Option[Player2], var stone: Option[Stone2], height: Int) {
  def code = (player, stone, height) match {
    case (None, None, 0) => 0
    case (Some(p), Some(s), h) if h > 0 => p.value * (h * 4 + s.value)
  }
}

object Field {
  lazy val empty = Field(None, None, 0)
  lazy val blackPebble = Field(Some(Player2.Black), Some(Stone2.Pebble), 1)
  lazy val blackRock = Field(Some(Player2.Black), Some(Stone2.Rock), 1)
  lazy val blackBoulder = Field(Some(Player2.Black), Some(Stone2.Boulder), 1)
  lazy val whitePebble = Field(Some(Player2.White), Some(Stone2.Pebble), 1)
  lazy val whiteRock = Field(Some(Player2.White), Some(Stone2.Rock), 1)
  lazy val whiteBoulder = Field(Some(Player2.White), Some(Stone2.Boulder), 1)

  def getOwner(fieldCode: Int): Option[Player2] = fieldCode match {
    case x if x > 0 => Some(Player2.White)
    case x if x < 0 => Some(Player2.Black)
    case _ => None
  }

  def getStone(fieldCode: Int): Option[Stone2] = Math.abs(fieldCode) % 4 match {
    case 0 => None
    case 1 => Some(Stone2.Pebble)
    case 2 => Some(Stone2.Rock)
    case 3 => Some(Stone2.Boulder)
  }

  def getHeight(fieldCode: Int) = Math.abs(fieldCode) / 4

  def fromCode(code: Int): Field = Field(getOwner(code), getStone(code), getHeight(code))
}
