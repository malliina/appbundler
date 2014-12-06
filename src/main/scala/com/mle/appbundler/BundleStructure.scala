package com.mle.appbundler

import java.nio.file.{Files, Path}

import com.mle.file.StorageFile

/**
 * @author Michael
 */
case class BundleStructure(outputDir: Path, displayName: String) {
  val appDir = outputDir / s"$displayName.app"
  val contentsDir = appDir / "Contents"
  val macOSDir = contentsDir / "MacOS"
  val javaDir = contentsDir / "Java"
  val pluginsDir = contentsDir / "PlugIns"
  val resourcesDir = contentsDir / "Resources"
  val infoPlistFile = contentsDir / "Info.plist"
  val pkgInfoFile = contentsDir / "PkgInfo"

  val dirs = Seq(appDir, contentsDir, macOSDir, javaDir, pluginsDir, resourcesDir)
  val files = Seq(infoPlistFile, pkgInfoFile)

  def prepare() = {
    AppBundler.delete(appDir)
    dirs.foreach(d => Files.createDirectories(d))
    files.foreach(f => Files.createFile(f))
  }
}