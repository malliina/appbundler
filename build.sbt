val appBundler = Project("appbundler", file("."))
  .enablePlugins(MavenCentralPlugin)
  .settings(
    gitUserName := "malliina",
    developerName := "Michael Skogberg",
    organization := "com.malliina",
    scalaVersion := "2.13.12",
    crossScalaVersions := Seq("2.12.18", scalaVersion.value),
    releaseCrossBuild := true,
    libraryDependencies ++= Seq(
      "com.malliina" %% "primitives" % "3.4.5",
      "org.slf4j" % "slf4j-api" % "2.0.9",
      "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
