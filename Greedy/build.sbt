name := "Greedy"

version := "1.0"

scalaVersion := "2.11.2"

mainClass in Compile := Some("com.gjos.scala.swoc.Main")

assemblySettings

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "com.googlecode.json-simple" % "json-simple" % "1.1.1"
)

unmanagedJars in Compile <++= baseDirectory map { base =>
  val libFolder = base / "lib"
  (libFolder ** "*.jar").classpath
}

javaOptions ++= Seq(
)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-optimize"
)

fork := true

connectInput := true