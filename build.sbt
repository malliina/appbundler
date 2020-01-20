val appBundler = Project("appbundler", file("."))
  .enablePlugins(MavenCentralPlugin)
  .settings(
    gitUserName := "malliina",
    developerName := "Michael Skogberg",
    organization := "com.malliina",
    scalaVersion := "2.13.1",
    crossScalaVersions := Seq("2.12.10", scalaVersion.value),
    releaseCrossBuild := true,
    resolvers += Resolver.bintrayRepo("malliina", "maven"),
    libraryDependencies ++= Seq(
      "com.malliina" %% "primitives" % "1.13.0",
      "org.slf4j" % "slf4j-api" % "1.7.30",
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    ),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, minor)) if minor >= 11 =>
          Seq("org.scala-lang.modules" %% "scala-xml" % "1.2.0")
        case _ =>
          Nil
      }
    }
  )
