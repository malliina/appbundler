package com.mle.appbundler

import java.nio.file.{StandardCopyOption, Files, Path, Paths}

import com.mle.file.{FileUtilities, StorageFile}
import com.mle.util.Log

/**
 * To create a .pkg package of your app, run `macPackage()`.
 *
 * @param rootOutput out dir
 * @param infoPlistConf
 * @param launchdConf
 * @param additionalDmgFiles files to include in the image, such as .DS_Store for styling and a .background
 * @param welcomeHtml wip
 * @param licenseHtml wip
 * @param conclusionHtml wip
 * @param deleteOutOnComplete
 */
case class Installer(rootOutput: Path,
                     infoPlistConf: InfoPlistConf,
                     launchdConf: Option[LaunchdConf] = None,
                     iconFile: Option[Path] = None,
                     additionalDmgFiles: Seq[Path] = Nil,
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
  val distributionFile = rootOutput / "Distribution.xml"
  val resourcesDir = rootOutput / "Resources"
  val scriptsDir = rootOutput / "Scripts"
  val pkgDir = rootOutput / "Pkg"
  val dmgSourceDir = rootOutput / "DmgContents"
  val packageFile = dmgSourceDir / s"Install $displayName.pkg"
  val dmgFile = rootOutput / s"$name-$version.dmg"
  private val launchdPlistPath = (Paths get "Library") / "LaunchDaemons" / s"$appIdentifier.plist"
  val launchdBuildPath = appOutput / launchdPlistPath
  val launchdInstallPath = (Paths get "/") / launchdPlistPath

  def macPackage(): Path = {
    AppBundler.delete(appOutput)
    Files.createDirectories(appOutput)
    Files.createDirectories(launchdBuildPath.getParent)
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
    AppBundler.createBundle(infoPlistConf, applicationsDir)
    //      val bundle = macAppDir
    //      val cmd = Seq("/usr/bin/SetFile", "-a", "B", bundle.toString)
    //      ExeUtils.execute(cmd, log)
    // runs pkgbuild
    Files.createDirectories(pkgDir)
    execute(pkgBuild)
    // runs productbuild
    AppBundler.delete(dmgSourceDir)
    Files.createDirectories(dmgSourceDir)
    execute(productBuild)

    /**
     * If the out directory used to build the .pkg is not deleted, the app will fail to install properly on the
     * development machine. I don't know why, I suspect I'm doing something wrong, but deleting the directory is a
     * workaround.
     */
    if (deleteOutOnComplete) {
      AppBundler.delete(appOutput)
    }
    iconFile.foreach(i => iconify(i, packageFile))
    log info s"Created $packageFile."
    packageFile
  }

  def dmgPackage(): Path = {
    val pkgFile = macPackage()
    additionalDmgFiles.foreach(file => Files.copy(file, dmgSourceDir / file.getFileName, StandardCopyOption.REPLACE_EXISTING))
    // hides the extension of the files in the .dmg image when opened in Finder
    (pkgFile +: additionalDmgFiles).map(hideExtension).foreach(execute)
    // runs hdiutil
    execute(hdiutil(displayName, dmgSourceDir, dmgFile))
    dmgFile
  }

  def withLaunchd() = copy(launchdConf = Some(LaunchdConf(appIdentifier, Seq(LaunchdConf.executable(displayName)))))

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

  /**
   * Sets icon `icon` to file `file`.
   *
   * @param icon icon file
   * @param file target file
   * @see http://apple.stackexchange.com/questions/6901/how-can-i-change-a-file-or-folder-icon-using-the-terminal
   */
  def iconify(icon: Path, file: Path) = {
    val iconStr = icon.toString
    val fileStr = file.toString
    val iconResource = Paths get "tmpicns.rsrc"
    val iconResourceStr = iconResource.toString
    val sip = Seq("/usr/bin/sips", "-i", iconStr)
    val deRez = Seq("/usr/bin/DeRez", "-only", "icns", iconStr, ">", iconResourceStr)
    val rez = Seq("/usr/bin/Rez", "-append", iconResourceStr, "-o", fileStr)
    val setIcon = Seq("/usr/bin/SetFile", "-a", "C", fileStr)
    Files.deleteIfExists(iconResource)
    Seq(sip, deRez, rez, setIcon).foreach(execute)
  }

  /**
   * A command that, when run, hides the extension of `file`.
   *
   * @return a command
   */
  def hideExtension(file: Path) = Seq(
    "/usr/bin/SetFile",
    "-a",
    "E",
    file.toString
  )

  /**
   * A command that creates a volume named `volumeName` of the contents in `sourceDir`.
   *
   * @param volumeName name of volume
   * @param sourceDir source dir
   * @param dmgOutFile output .dmg file
   * @return a command
   */
  def hdiutil(volumeName: String, sourceDir: Path, dmgOutFile: Path) = Seq(
    "/usr/bin/hdiutil",
    "create",
    "-volname",
    volumeName,
    "-srcfolder",
    sourceDir.toString,
    "-ov",
    dmgOutFile.toString
  )

  def writePreInstall(identifier: String, launchPlist: Path, buildDest: Path) =
    scriptify(buildDest) {
      s"""#!/bin/sh
        |set -e
        |if /bin/launchctl list "$identifier" &> /dev/null; then
        |    /bin/launchctl unload "$launchPlist"
        |fi"""
    }

  def writePostInstall(launchPlist: Path, buildDest: Path) =
    scriptify(buildDest) {
      s"""#!/bin/sh
        |set -e
        |/bin/launchctl load "$launchPlist"
      """
    }

  def scriptify(buildDest: Path)(f: => String) = {
    FileUtilities.writerTo(buildDest)(w => w.println(f.stripMargin))
    buildDest.toFile.setExecutable(true, false)
  }

  def execute(command: Seq[String]) = ExeUtils.execute(command, log)
}