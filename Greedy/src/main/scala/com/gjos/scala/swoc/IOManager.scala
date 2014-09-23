package com.gjos.scala.swoc

import java.io._
import scala.annotation.tailrec

class IOManager(val inReader: BufferedReader, val outWriter: BufferedWriter) extends AutoCloseable {

  val newline = System.lineSeparator()

  @tailrec final def readLine(): String = {
    val line = inReader.readLine()
    if (line != null && line.isEmpty) readLine() else line
  }

  def writeLine(message: String) {
    outWriter.write(message)
    outWriter.write(newline)
    outWriter.flush()
  }

  def close() {
    inReader.close()
    outWriter.close()
  }
}

object IOManager {
  def runMode() = {
    val input = new InputStreamReader(System.in)
    val output = new OutputStreamWriter(System.out)
    val bufferedIn = new BufferedReader(input)
    val bufferedOut = new BufferedWriter(output)
    new IOManager(bufferedIn, bufferedOut)
  }

  def fileMode(f: File): (IOManager, () => String) = {
    val input = new InputStreamReader(new FileInputStream(f))
    val baos = new ByteArrayOutputStream()
    val output = new OutputStreamWriter(baos)
    val bufferedIn = new BufferedReader(input)
    val bufferedOut = new BufferedWriter(output)
    (new IOManager(bufferedIn, bufferedOut), baos.toString)
  }
}