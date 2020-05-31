val appBundler = Project("appbundler", file("."))
  .enablePlugins(MavenCentralPlugin)
  .settings(
    gitUserName := "malliina",
    developerName := "Michael Skogberg",
    organization := "com.malliina",
    scalaVersion := "2.13.2",
    crossScalaVersions := Seq("2.12.10", scalaVersion.value),
    releaseCrossBuild := true,
    resolvers += Resolver.bintrayRepo("malliina", "maven"),
    libraryDependencies ++= Seq(
      "com.malliina" %% "primitives" % "1.17.0",
      "org.slf4j" % "slf4j-api" % "1.7.30",
      "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
      "org.scalameta" %% "munit" % "0.7.7" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
