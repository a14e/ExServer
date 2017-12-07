name := "ExServer"

version := "0.1"

scalaVersion := "2.12.4"

val circeVersion = "0.8.0"
val akkaVersion = "2.4.17"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,

  "com.iheart" %% "ficus" % "1.4.3",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-java8" % circeVersion,

  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",

  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.3.0",

  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test

)