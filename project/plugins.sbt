// -*- scala -*-

resolvers += Classpaths.typesafeResolver

resolvers += "scct-github-repository" at "http://mtkopone.github.com/scct/maven-repo"

addSbtPlugin("reaktor" % "sbt-scct" % "0.2-SNAPSHOT")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.2.0-SNAPSHOT")

resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
