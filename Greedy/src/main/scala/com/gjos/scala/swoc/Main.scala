package com.gjos.scala.swoc

import gos.bot.{Bot, Engine}

object Main extends App {
  val bot = new Bot
  val engine = new Engine(bot)
  try {
    engine.run()
  } catch {
    case e: Exception => e.printStackTrace()
  }
}
