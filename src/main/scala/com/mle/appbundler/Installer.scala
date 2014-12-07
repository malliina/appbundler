package com.mle.appbundler

import java.nio.file.{Files, Path, Paths}

import com.mle.file.{FileUtilities, StorageFile}
import com.mle.util.Log

/**
 * To create a .pkg package of your app, run `macPackage()`.
 */
case class Installer(rootOutput: Path,
                     organization: String,
                     infoPlistConf: InfoPlistConf,
                     launchdConf: Option[LaunchdConf] = None,
                     welcomeHtml: Option[Path] = None,
                     licenseHtml: Option[Path] = None,
                     conclusionHtml: Option[Path] = None,
                     deleteOutOnComplete: Boolean = true) extends Log {
  val appOutput = rootOutput / "out"
  val applicationsDir = appOutput / "Applications"
  val displayName = infoPlistConf.displayName
  val name = infoPlistConf.name
  val version = infoPlistConf.version
  val appIdentifier = infoPlistConf.identifier
  val structure = BundleStructure(applicationsDir, displayName)
  val distributionFile = rootOutput / "Distribution.xml"
  val resourcesDir = rootOutput / "Resources"
  val scriptsDir = rootOutput / "Scripts"
  val pkgDir = rootOutput / "Pkg"
  val packageFile = rootOutput / s"$name-$version.pkg"
  private val launchdPlistPath = (Paths get "Library") / "LaunchDaemons" / s"$appIdentifier.plist"
  val launchdBuildPath = appOutput / launchdPlistPath
  val launchdInstallPath = (Paths get "/") / launchdPlistPath

  def macPackage() = {
    AppBundler.delete(appOutput)
    Files.createDirectories(appOutput)
    Distribution.writeDistribution(DistributionConf(appIdentifier, displayName, name), distributionFile)
    AppBundler.copyFileOrResource(welcomeHtml, "welcome.html", resourcesDir / "welcome.html")
    AppBundler.copyFileOrResource(licenseHtml, "license.html", resourcesDir / "license.html")
    AppBundler.copyFileOrResource(conclusionHtml, "conclusion.html", resourcesDir / "conclusion.html")
    Files.createDirectories(scriptsDir)
    launchdConf.foreach(launchd => {
      Files.createDirectories(launchdBuildPath.getParent)
      launchd.write(launchdBuildPath)
      writePreInstall(appIdentifier, launchdInstallPath, scriptsDir / "preinstall")
      writePostInstall(launchdInstallPath, scriptsDir / "postinstall")
    })
    AppBundler.createBundle(structure, infoPlistConf)
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
    if (deleteOutOnComplete) {
      AppBundler.delete(appOutput)
    }
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

  def writePreInstall(identifier: String, launchPlist: Path, buildDest: Path) =
    scriptify(launchPlist, buildDest)(p => {
      s"""#!/bin/sh
        |set -e
        |if /bin/launchctl list "$identifier" &> /dev/null; then
        |    /bin/launchctl unload "$p"
        |fi"""
    })

  def writePostInstall(launchPlist: Path, buildDest: Path) =
    scriptify(launchPlist, buildDest)(p => {
      s"""#!/bin/sh
        |set -e
        |/bin/launchctl load "$p"
      """
    })

  def scriptify(launchPlist: Path, buildDest: Path)(f: Path => String) = {
    FileUtilities.writerTo(buildDest)(w => w.println(f(launchPlist).stripMargin))
    buildDest.toFile.setExecutable(true, false)
  }
}