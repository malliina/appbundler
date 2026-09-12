package com.malliina.appbundler

import java.nio.file.{Files, Path, Paths, StandardCopyOption}

import org.slf4j.LoggerFactory

/** To create a .pkg package of your app, run `macPackage()`.
  *
  * @param rootOutput
  *   out dir
  * @param additionalDmgFiles
  *   files to include in the image, such as .DS_Store for styling and a .background
  */
case class Installer(
  rootOutput: Path,
  infoPlistConf: InfoPlistConf,
  launchdConf: Option[LaunchdConf] = None,
  iconFile: Option[Path] = None,
  additionalDmgFiles: Seq[FileMapping] = Nil,
  welcomeHtml: Option[Path] = None,
  licenseHtml: Option[Path] = None,
  conclusionHtml: Option[Path] = None,
  deleteOutOnComplete: Boolean = true
):
  private val log = LoggerFactory.getLogger(getClass.getName.stripSuffix("$"))
  private val appOutput = rootOutput / "out"
  private val applicationsDir = appOutput / "Applications"
  val displayName = infoPlistConf.displayName
  val name = infoPlistConf.name
  val version = infoPlistConf.version
  private val appIdentifier = infoPlistConf.identifier
  private val distributionFile = rootOutput / "Distribution.xml"
  private val resourcesDir = rootOutput / "Resources"
  private val scriptsDir = rootOutput / "Scripts"
  private val pkgDir = rootOutput / "Pkg"
  private val dmgSourceDir = rootOutput / "DmgContents"
  private val packageFile = dmgSourceDir / s"Install $displayName.pkg"
  private val dmgFile = rootOutput / s"$name-$version.dmg"
  private val rootPath = Paths.get("/")

  private def macPackage(): Path =
    AppBundler.delete(appOutput)
    Files.createDirectories(appOutput)
    Distribution.writeDistribution(
      DistributionConf(appIdentifier, displayName, name),
      distributionFile
    )
    Files.createDirectories(resourcesDir)
    Files.createDirectories(scriptsDir)
    launchdConf.foreach: launchd =>
      val launchdInstallPath = launchd.plistDir / s"$appIdentifier.plist"
      val launchdBuildPath = appOutput / rootPath.relativize(launchdInstallPath)
      Files.createDirectories(launchdBuildPath.getParent)
      launchd.write(launchdBuildPath)
      writePreInstall(appIdentifier, launchdInstallPath, scriptsDir / "preinstall")
      writePostInstall(launchdInstallPath, scriptsDir / "postinstall")
    AppBundler.createBundle(infoPlistConf, applicationsDir)
    // runs pkgbuild
    Files.createDirectories(pkgDir)
    execute(pkgBuild)
    // runs productbuild
    AppBundler.delete(dmgSourceDir)
    Files.createDirectories(dmgSourceDir)
    execute(productBuild)

    /** If the out directory used to build the .pkg is not deleted, the app will fail to install
      * properly on the development machine. I don't know why, I suspect I'm doing something wrong,
      * but deleting the directory is a workaround.
      */
    if deleteOutOnComplete then AppBundler.delete(appOutput)
    iconFile.foreach(i => iconify(i, packageFile))
    packageFile

  /** @return
    *   the built .dmg file
    */
  def dmgPackage(): Path = buildDmg(macPackage(), displayName, dmgFile)

  def buildDmg(pkgFile: Path, displayName: String, outFile: Path) =
    val dmgRoot = pkgFile.getParent
    val absolutes = additionalDmgFiles.map: fm =>
      fm.copy(after = dmgRoot / fm.after)
    absolutes.foreach: fm =>
      val dest = fm.after
      Option(dest.getParent).foreach(d => Files.createDirectories(d))
      Files.copy(fm.before, dest, StandardCopyOption.REPLACE_EXISTING)
    // hides the extension of the files in the .dmg image when opened in Finder
    (pkgFile +: absolutes.map(_.after)).map(hideExtension).foreach(execute)
    // runs hdiutil
    execute(hdiutil(displayName, dmgRoot, outFile))
    outFile

  def withLaunchd() =
    copy(launchdConf = Some(LaunchdConf(appIdentifier, Seq(LaunchdConf.executable(displayName)))))

  private def pkgBuild = Seq(
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

  private def productBuild = Seq(
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

  /** Sets icon `icon` to file `file`.
    *
    * @param icon
    *   icon file
    * @param file
    *   target file
    * @see
    *   http://apple.stackexchange.com/questions/6901/how-can-i-change-a-file-or-folder-icon-using-the-terminal
    */
  private def iconify(icon: Path, file: Path): Unit =
    val iconStr = icon.toString
    val fileStr = file.toString
    val icnsOut = "tmpicns.icns"
    val iconResource = Paths.get("tmpicns.rsrc")
    val iconResourceStr = iconResource.toString

    // https://apple.stackexchange.com/a/10783
    val sipNew = Seq("/usr/bin/sips", "-s", "format", "icns", iconStr, "--out", icnsOut)
    val iconSet = Seq("/usr/local/bin/seticon", "-d", icnsOut, iconStr)
//    val sip = Seq("/usr/bin/sips", "-i", iconStr)
    val deRez = Seq("/usr/bin/DeRez", "-only", "icns", iconStr)
    val rez = Seq("/usr/bin/Rez", "-append", iconResourceStr, "-o", fileStr)
    val setIcon = Seq("/usr/bin/SetFile", "-a", "C", fileStr)
    Files.deleteIfExists(iconResource)
    execute(sipNew)
    execute(iconSet)
    ExeUtils.executeRedirected(deRez, iconResource, log)
//    execute(rez)
    Seq(rez, setIcon).foreach(execute)

  /** A command that, when run, hides the extension of `file`.
    *
    * @return
    *   a command
    */
  private def hideExtension(file: Path) = Seq(
    "/usr/bin/SetFile",
    "-a",
    "E",
    file.toString
  )

  /** A command that creates a volume named `volumeName` of the contents in `sourceDir`.
    *
    * @param volumeName
    *   name of volume
    * @param sourceDir
    *   source dir
    * @param dmgOutFile
    *   output .dmg file
    * @return
    *   a command
    */
  private def hdiutil(volumeName: String, sourceDir: Path, dmgOutFile: Path) = Seq(
    "/usr/bin/hdiutil",
    "create",
    "-volname",
    volumeName,
    "-srcfolder",
    sourceDir.toString,
    "-ov",
    dmgOutFile.toString
  )

  private def writePreInstall(identifier: String, launchPlist: Path, buildDest: Path) =
    scriptify(buildDest):
      s"""#!/bin/sh
         |set -e
         |if /bin/launchctl list "$identifier" &> /dev/null; then
         |    /bin/launchctl unload "$launchPlist"
         |fi"""

  private def writePostInstall(launchPlist: Path, buildDest: Path) =
    scriptify(buildDest):
      s"""#!/bin/sh
         |set -e
         |/bin/launchctl load "$launchPlist"
      """

  private def scriptify(buildDest: Path)(f: => String) =
    AppBundler.writerTo(buildDest)(w => w.println(f.stripMargin))
    buildDest.toFile.setExecutable(true, false)

  private def execute(command: Seq[String]): Unit = ExeUtils.execute(command, log)
