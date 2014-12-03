package com.mle.appbundler

import java.nio.file.{Files, Path}

import com.mle.file.{FileUtilities, StorageFile}
import com.mle.util.Log

/**
 * To create a .pkg package of your app, run `macPackage()`.
 */
case class Installer(rootOutput: Path,
                     displayName: String,
                     name: String,
                     version: String,
                     organization: String,
                     appIdentifier: String,
                     welcomeHtml: Option[Path] = None,
                     licenseHtml: Option[Path] = None,
                     conclusionHtml: Option[Path] = None,
                     conf: BundleStructure,
                     infoPlistConf: InfoPlistConf) extends Log {
  val appOutput = rootOutput / "out"
  val applicationsDir = appOutput / "Applications"
  val dotAppDir = applicationsDir / s"$displayName.app"
  val contentsDir = dotAppDir / "Contents"
  val distributionFile = rootOutput / "Distribution.xml"
  val resourcesDir = rootOutput / "Resources"
  val scriptsDir = rootOutput / "Scripts"
  val pkgDir = rootOutput / "Pkg"
  val packageFile = rootOutput / s"$name-$version.pkg"
  val launchPlistFile = appOutput / "Library" / "LaunchDaemons" / s"$appIdentifier.plist"

  def macPackage() = {
    Files.createDirectories(appOutput)
    Distribution.writeDistribution(DistributionConf(appIdentifier, organization, displayName, name), distributionFile)
    AppBundler.copyFileOrResource(welcomeHtml, "welcome.html", resourcesDir / "welcome.html")
    AppBundler.copyFileOrResource(licenseHtml, "license.html", resourcesDir / "license.html")
    AppBundler.copyFileOrResource(conclusionHtml, "conclusion.html", resourcesDir / "conclusion.html")
    Files.createDirectories(scriptsDir)
    writePreInstall(appIdentifier, launchPlistFile, scriptsDir / "preinstall")
    writePostInstall(launchPlistFile, scriptsDir / "postinstall")
    PlistWriter.writeDaemon(appIdentifier, displayName, launchPlistFile)
    AppBundler.createBundle(conf, infoPlistConf)
    //      val bundle = macAppDir
    //      val cmd = Seq("/usr/bin/SetFile", "-a", "B", bundle.toString)
    //      ExeUtils.execute(cmd, log)
    Files.createDirectories(pkgDir)
    ExeUtils.execute(pkgBuild, log)
    ExeUtils.execute(productBuild, log)

    /**
     * If the out directory used to build the .pkg is not deleted, the app will fail to install properly on the
     * development machine. I don't know why, I suspect I'm doing something wrong, but deleting the directory is a
     * workaround.
     */
    AppBundler.delete(appOutput)
    log info s"Created $packageFile"
    packageFile
  }

  def pkgBuild = Seq(
    "/usr/bin/pkgbuild",
    "--root",
    appOutput.toString,
    "--identifier",
    appIdentifier,
    "--version",
    version,
    "--scripts",
    scriptsDir.toString,
    "--ownership",
    "recommended",
    (pkgDir / s"$name.pkg").toString
  )

  def productBuild = Seq(
    "/usr/bin/productbuild",
    "--distribution",
    distributionFile.toString,
    "--resources",
    resourcesDir.toString,
    "--version",
    version,
    "--package-path",
    pkgDir.toString,
    packageFile.toString
  )


  def writePostInstall(launchPlist: Path, buildDest: Path) = {
    scriptify(launchPlist, buildDest)(p => {
      s"""#!/bin/sh
        |set -s
        |/bin/launchctl load "$p"
      """
    })
  }

  def writePreInstall(identifier: String, launchPlist: Path, buildDest: Path) =
    scriptify(launchPlist, buildDest)(p => {
      s"""#!/bin/sh
        |set -e
        |if /bin/launchctl list "$identifier" &> /dev/null; then
        |    /bin/launchctl unload "$p"
        |fi"""
    })

  def scriptify(launchPlist: Path, buildDest: Path)(f: Path => String) = {
    FileUtilities.writerTo(buildDest)(w => w.println(f(launchPlist).stripMargin))
    buildDest.toFile.setExecutable(true, false)
  }
}
