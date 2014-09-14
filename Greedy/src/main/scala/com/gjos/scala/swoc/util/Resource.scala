package com.gjos.scala.swoc.util

import java.io.File

object Resource {
  def testResource(name: String) = new File(s"src/test/resources/$name")
}
