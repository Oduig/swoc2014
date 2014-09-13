package com.gjos.scala.swoc

import java.io.{BufferedReader, InputStreamReader}


object Main extends App {
  private val inStreamReader = new InputStreamReader(System.in)
  private val inReader = new BufferedReader(inStreamReader)
  val bot = new Bot(None)
  val engine = new Engine(bot, inReader, inStreamReader)
  try {
    engine.run()
  } catch {
    case e: Exception => e.printStackTrace()
  }
}
