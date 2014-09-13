package com.gjos.scala.swoc


object Main extends App {
  val bot = new Bot(None)
  val engine = new Engine(bot)
  try {
    engine.run()
  } catch {
    case e: Exception => e.printStackTrace()
  }
}
