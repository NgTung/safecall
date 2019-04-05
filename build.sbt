name := "safecall"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.1",
  "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
)