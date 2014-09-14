package com.gjos.scala.swoc

import java.io.{BufferedReader, InputStreamReader}


object Main extends App {
  val ioManager = IOManager.runMode
  val bot = new Bot(None)
  val engine = new Engine(bot, ioManager)
  try {
    engine.run()
  } catch {
    case e: RuntimeException => e.printStackTrace()
  }
}
