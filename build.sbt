lazy val appBundler = Project("appbundler", file("."))
    .enablePlugins(MavenCentralPlugin)

gitUserName := "malliina"
developerName := "Michael Skogberg"
organization := "com.malliina"
scalaVersion := "2.12.8"
crossScalaVersions := Seq("2.11.12", scalaVersion.value)
releaseCrossBuild := true
resolvers += Resolver.bintrayRepo("malliina", "maven")
libraryDependencies ++= Seq(
  "com.malliina" %% "primitives" % "1.8.1",
  "org.slf4j" % "slf4j-api" % "1.7.25"
)
libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, minor)) if minor >= 11 =>
      Seq("org.scala-lang.modules" %% "scala-xml" % "1.1.1")
    case _ =>
      Nil
  }
}
