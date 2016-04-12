import com.malliina.sbtutils.SbtProjects
import sbt.Keys._
import sbt._

object AppBundlerBuild extends Build {

  lazy val p = SbtProjects.testableProject("appbundler")
    .enablePlugins(bintray.BintrayPlugin)
    .settings(projectSettings: _*)

  lazy val projectSettings = Seq(
    version := "0.9.2",
    organization := "com.malliina",
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq("2.10.6", scalaVersion.value),
    fork in Test := true,
    libraryDependencies ++= Seq("com.malliina" %% "util" % "2.2.3"),
    updateOptions := updateOptions.value.withCachedResolution(true),
    licenses +=("MIT", url("http://opensource.org/licenses/MIT"))
  )
}
