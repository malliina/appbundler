import com.malliina.sbtutils.SbtProjects
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}

lazy val appBundler = SbtProjects.mavenPublishProject("appbundler")

gitUserName := "malliina"
developerName := "Michael Skogberg"
organization := "com.malliina"
scalaVersion := "2.12.2"
crossScalaVersions := Seq("2.10.6", "2.11.11", scalaVersion.value)
resolvers += Resolver.bintrayRepo("malliina", "maven")
libraryDependencies += "com.malliina" %% "util" % "2.6.0"
libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, minor)) if minor >= 11 =>
      Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.6")
    case _ =>
      Nil
  }
}
