import com.malliina.sbtutils.SbtProjects
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}

lazy val appBundler = SbtProjects.mavenPublishProject("appbundler")

gitUserName := "malliina"
developerName := "Michael Skogberg"
organization := "com.malliina"
scalaVersion := "2.12.4"
crossScalaVersions := Seq("2.10.6", "2.11.11", scalaVersion.value)
releaseCrossBuild := true
resolvers += Resolver.bintrayRepo("malliina", "maven")
libraryDependencies ++= Seq(
  "com.malliina" %% "primitives" % "1.3.2",
  "org.slf4j" % "slf4j-api" % "1.7.25"
)
libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, minor)) if minor >= 11 =>
      Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.6")
    case _ =>
      Nil
  }
}
