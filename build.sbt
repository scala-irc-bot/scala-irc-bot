// -*- scala -*-

name := "scala-irc-bot"

organization := "net.mtgto"

version := "0.2.1"

scalaVersion := "2.10.2"

crossScalaVersions := Seq("2.10.2")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies := Seq(
  "org.pircbotx" % "pircbotx" % "1.8",
  "com.twitter" %% "util-eval" % "6.2.0",
  "com.typesafe.akka" %% "akka-actor" % "2.1.0",
  "ch.qos.logback" % "logback-classic" % "1.0.7",
  "junit" % "junit" % "4.10" % "test",
  "org.specs2" %% "specs2" % "1.14" % "test"
)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "UTF8", "-feature")

unmanagedBase in Runtime <<= baseDirectory { base => base / "bots" }

initialCommands := "import net.mtgto.irc._"

ScctPlugin.instrumentSettings

testOptions in ScctTest += Tests.Argument(TestFrameworks.Specs2, "console", "junitxml")

org.scalastyle.sbt.ScalastylePlugin.Settings

publishTo := Some(Resolver.file("file", new File("maven/")))
