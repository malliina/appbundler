import com.malliina.sbtutils.SbtProjects
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}
import sbt.Keys._
import sbt._

object AppBundlerBuild {

  lazy val appBundler = SbtProjects.mavenPublishProject("appbundler")
    .settings(projectSettings: _*)

  lazy val projectSettings = Seq(
    version := "0.9.3",
    organization := "com.malliina",
    scalaVersion := "2.11.8",
    developerName := "Michael Skogberg",
    gitUserName := "malliina",
    crossScalaVersions := Seq("2.10.6", scalaVersion.value),
    fork in Test := true,
    libraryDependencies ++= Seq("com.malliina" %% "util" % "2.2.3"),
    resolvers += Resolver.bintrayRepo("malliina", "maven")
  )
}
