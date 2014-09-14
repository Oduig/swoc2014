package com.gjos.scala.swoc.util

object Stopwatch {
  val t0 = System.currentTimeMillis
  private var previous = t0

  def sinceStart = System.currentTimeMillis - t0

  def sinceLast = {
    val now = System.currentTimeMillis
    val dt = now - previous
    previous = now
    dt
  }

  def tell(message: String = "") = {
    //println(prettyPrint(sinceLast))
    //if (message.nonEmpty) println(message)
  }

  private def prettyPrint(t: Long) = f"Stopwatch: ${t / 1000f}%.2f seconds"
}
