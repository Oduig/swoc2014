package com.gjos.scala.swoc


object Main extends App {
  val bot = new Bot2
  val engine = new Engine2(bot)
  try {
    engine.run()
  } catch {
    case e: Exception => e.printStackTrace()
  }
}
