import com.malliina.sbtutils.SbtProjects
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}
import sbt.Keys._
import sbt._

object AppBundlerBuild {

  lazy val appBundler = SbtProjects.mavenPublishProject("appbundler")
    .settings(projectSettings: _*)

  lazy val projectSettings = Seq(
    gitUserName := "malliina",
    developerName := "Michael Skogberg",
    organization := "com.malliina",
    scalaVersion := "2.11.8",
    crossScalaVersions := Seq("2.10.6", scalaVersion.value),
    libraryDependencies ++= Seq(
      "com.malliina" %% "util" % "2.2.3"
    ),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, minor)) if minor >= 11 =>
          Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.6")
        case _ =>
          Nil
      }
    }
  )
}
