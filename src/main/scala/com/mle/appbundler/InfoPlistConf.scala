package com.mle.appbundler

import java.nio.file.{Path, Paths}

import com.mle.appbundler.InfoPlistConf.{DEFAULT_EXECUTABLE_NAME, DEFAULT_JAVA}

/**
 * @author mle
 */
case class InfoPlistConf(displayName: String,
                         name: String,
                         identifier: String,
                         version: String,
                         mainClass: String,
                         jars: Seq[Path],
                         javaHome: Path = DEFAULT_JAVA,
                         jvmOptions: Seq[String] = Nil,
                         jvmArguments: Seq[String] = Nil,
                         iconFile: Option[Path] = None,
                         executableName: String = DEFAULT_EXECUTABLE_NAME,
                         workingDir: Option[String] = None,
                         copyright: String = "",
                         shortVersion: String = "1.0",
                         hideDock: Boolean = false,
                         highResolutionCapable: Boolean = false,
                         supportsAutomaticGraphicsSwitching: Boolean = false,
                         minimumSystemVersion: Option[String] = None,
                         applicationCategory: Option[String] = None,
                         signature: String = "????",
                         additional: Map[String, String] = Map.empty,
                         additionalArrays: Map[String, Seq[String]] = Map.empty) {
  val jvmRuntimeDirName = Option(AppBundler.resolveJavaDirectory(javaHome).getParent)
    .flatMap(p => Option(p.getParent)) getOrElse javaHome

  private def map: Map[String, String] = Map(
    "CFBundleDevelopmentRegion" -> "English",
    "CFBundleExecutable" -> executableName,
    "CFBundleIconFile" -> iconFile.fold(AppBundler.DEFAULT_ICON_NAME)(p => p.getFileName.toString),
    "CFBundleIdentifier" -> identifier,
    "CFBundleDisplayName" -> displayName,
    "CFBundleInfoDictionaryVersion" -> "6.0",
    "CFBundleName" -> name,
    "CFBundlePackageType" -> AppBundler.OS_TYPE_CODE,
    "CFBundleShortVersionString" -> shortVersion,
    "CFBundleSignature" -> signature,
    "CFBundleVersion" -> "1",
    "NSHumanReadableCopyright" -> copyright,
    "JVMRuntime" -> jvmRuntimeDirName.getFileName.toString,
    "JVMMainClassName" -> mainClass,
    "LSUIElement" -> hideDock.toString
  )

  private def optMap = Seq(
    applicationCategory.map(ac => "LSApplicationCategoryType" -> ac),
    minimumSystemVersion.map(msv => "LSMinimumSystemVersion" -> msv),
    workingDir.map(wd => "WorkingDirectory" -> wd)
  ).flatten.toMap

  private def boolMap = Seq(
    if (hideDock) Some("LSUIElement" -> hideDock) else None,
    if (highResolutionCapable) Some("NSHighResolutionCapable" -> highResolutionCapable) else None,
    if (supportsAutomaticGraphicsSwitching) Some("NSSupportsAutomaticGraphicsSwitching" -> supportsAutomaticGraphicsSwitching) else None
  ).flatten.map(pair => pair._1 -> pair._2.toString).toMap

  def singles = map ++ optMap ++ boolMap ++ additional

  def arrays = Map(
    "JVMOptions" -> jvmOptions,
    "JVMArguments" -> jvmArguments
  ) ++ additionalArrays
}

object InfoPlistConf {
  val DEFAULT_JAVA = Paths get "/usr/libexec/java_home"
  val DEFAULT_EXECUTABLE_NAME = "JavaAppLauncher"
}