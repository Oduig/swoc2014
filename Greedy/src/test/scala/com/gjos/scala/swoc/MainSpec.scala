package com.gjos.scala.swoc

import org.scalatest.{Matchers, WordSpec}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import com.gjos.scala.swoc.util.Resource
import com.gjos.scala.swoc.protocol.{Player, BoardLocation, MoveType, Move}

class MainSpec extends WordSpec with Matchers {

  "Main" should {
    "run without timing out" in {
      val (ioManager, output) = IOManager.fileMode(Resource.testResource("replay-timeout.txt"))
      val bot = new Bot(None)
      val engine = new Engine(bot, ioManager)

      val maxRuntime = 2000.millis
      Await.ready(Future(engine.run()), maxRuntime)
      val outLines = output().split(ioManager.newline).toList
      outLines.size should be (1)
    }
  }
}
