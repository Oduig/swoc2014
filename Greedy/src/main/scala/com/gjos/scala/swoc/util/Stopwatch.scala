package com.gjos.scala.swoc.util

class Stopwatch(val outputEnabled: Boolean) {
  val t0 = System.currentTimeMillis
  private var previous = t0
  //private var pauseTime = 0l

  def sinceStart = {
    val now = System.currentTimeMillis
    previous = now
    now - t0
  }

//  private var paused = false
//  def pause = if (!paused) {
//    pauseTime = System.currentTimeMillis
//    paused = true
//  } else throw new Exception()
//
//  def resume = if (paused) {
//    previous += System.currentTimeMillis - pauseTime
//    paused = false
//  } else throw new Exception()

  def sinceLast = {
    val now = System.currentTimeMillis
    val dt = now - previous
    previous = now
    dt
  }

  def tell(message: String = "") = if (outputEnabled) {
    println("|" + prettyPrint(sinceLast) + "|" + message)
  }

  private def prettyPrint(t: Long) = f"Stopwatch: ${t / 1000f}%.2f seconds"
}

object Stopwatch {
  private lazy val default = new Stopwatch(false)
  private lazy val debug = new Stopwatch(true)
  def apply(printEnabled: Boolean = false) = if (printEnabled) debug else default
}