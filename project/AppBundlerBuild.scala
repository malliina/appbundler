import com.mle.sbtutils.{SbtUtils, SbtProjects}
import sbt._
import sbt.Keys._

object AppBundlerBuild extends Build {

  lazy val p = SbtProjects.mavenPublishProject("appbundler").settings(projectSettings: _*)

  lazy val projectSettings = Seq(
    SbtUtils.gitUserName := "malliina",
    SbtUtils.developerName := "Michael Skogberg",
    version := "0.0.3",
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.10.4", scalaVersion.value),
    fork in Test := true,
    libraryDependencies ++= Seq("com.github.malliina" %% "util" % "1.5.0")
  )
}
