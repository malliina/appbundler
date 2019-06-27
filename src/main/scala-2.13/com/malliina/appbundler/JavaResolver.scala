package com.malliina.appbundler

import java.nio.file.{Files, Path, Paths}

import scala.sys.process.Process

object JavaResolver extends JavaResolver

trait JavaResolver {
  /** Converts `javaHome` to a jdk/Contents/Home path.
    *
    * Parameter `javaHome` may be any of the following:
    * a) a script that returns the directory, like /usr/libexec/java_home
    * b) a jdk8 dir
    * c) a jdk8/Contents/Home dir
    *
    * @param javaHome a valid java home path, or `javaHome`
    * @return
    */
  def resolveJavaDirectory(javaHome: Path): Path =
    if (!Files.isDirectory(javaHome) && Files.isExecutable(javaHome)) {
      Paths get Process(javaHome.toString).lazyLines.head
    } else {
      val expectedName = "Home"
      if (javaHome.getFileName.toString != expectedName) {
        val maybeHome = javaHome / "Contents" / expectedName
        if (Files.isDirectory(maybeHome)) maybeHome
        else javaHome
      } else {
        javaHome
      }
    }
}
