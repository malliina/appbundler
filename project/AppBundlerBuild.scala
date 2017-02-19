import com.malliina.sbtutils.{SbtProjects, SbtUtils}
import sbt.Keys._
import sbt._

object AppBundlerBuild {

  lazy val p = SbtProjects.mavenPublishProject("appbundler")
    .settings(projectSettings: _*)

  lazy val projectSettings = Seq(
    version := "0.9.2",
    SbtUtils.gitUserName := "malliina",
    SbtUtils.developerName := "Michael Skogberg",
    organization := "com.malliina",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "com.malliina" %% "util" % "2.5.0",
      "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
    )
  )
}
