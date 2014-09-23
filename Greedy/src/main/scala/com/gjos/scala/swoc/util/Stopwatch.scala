package com.gjos.scala.swoc.util

class Stopwatch(val outputEnabled: Boolean) {
  val t0 = System.currentTimeMillis
  private var previous = t0

  private def sinceStart = {
    val now = System.currentTimeMillis
    previous = now
    now - t0
  }

  private def sinceLast = {
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
  lazy val debugStopwatch = new Stopwatch(true)
  def apply() = debugStopwatch
}