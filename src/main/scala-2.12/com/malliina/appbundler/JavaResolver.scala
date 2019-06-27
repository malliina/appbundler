package com.malliina.appbundler

import java.nio.file.{Files, Path, Paths}

import scala.sys.process.Process

object JavaResolver extends JavaResolver

trait JavaResolver {
  def resolveJavaDirectory(javaHome: Path): Path =
    if (!Files.isDirectory(javaHome) && Files.isExecutable(javaHome)) {
      Paths get Process(javaHome.toString).lines.head
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
