name := "Greedy"

version := "1.0"

scalaVersion := "2.11.2"

mainClass in Compile := Some("com.gjos.scala.swoc.Main")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)

unmanagedJars in Compile <++= baseDirectory map { base =>
  val libFolder = base / "lib"
  (libFolder ** "*.jar").classpath
}

javaOptions ++= Seq(
)

fork in run := true

connectInput in run := true