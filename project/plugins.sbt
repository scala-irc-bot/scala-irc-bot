// -*- scala -*-

resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.github.scct" % "sbt-scct" % "0.2")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.2.0")

resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
