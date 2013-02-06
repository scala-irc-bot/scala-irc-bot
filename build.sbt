// -*- scala -*-

name := "scala-irc-bot"

organization := "net.mtgto"

version := "0.2.0-SNAPSHOT"

scalaVersion := "2.10.0"

crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.0")

resolvers += "twitter repos" at "http://maven.twttr.com"

libraryDependencies := Seq(
  "org.pircbotx" % "pircbotx" % "1.7",
  "com.twitter" % "util-eval" % "5.3.10",
  "ch.qos.logback" % "logback-classic" % "1.0.7",
  "junit" % "junit" % "4.10" % "test",
  "org.specs2" %% "specs2" % "1.12.3" % "test"
)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "UTF8")

unmanagedBase in Runtime <<= baseDirectory { base => base / "bots" }

initialCommands := "import net.mtgto.irc._"

ScctPlugin.instrumentSettings

testOptions in ScctTest += Tests.Argument(TestFrameworks.Specs2, "console", "junitxml")

org.scalastyle.sbt.ScalastylePlugin.Settings

publishTo := Some(Resolver.file("file", new File("maven/")))
