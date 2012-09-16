// -*- scala -*-

name := "scala-irc-bot"

organization := "net.mtgto"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.2"

resolvers += "twitter repos" at "http://maven.twttr.com"

libraryDependencies := Seq(
  "pircbot" % "pircbot" % "1.5.0",
  "com.twitter" % "util-eval" % "5.3.10",
  "ch.qos.logback" % "logback-classic" % "1.0.7",
  "org.specs2" %% "specs2" % "1.12.1" % "test"
)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "UTF8")

unmanagedBase in Runtime <<= baseDirectory { base => base / "bots" }

initialCommands := "import net.mtgto.irc._"
