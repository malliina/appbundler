package com.malliina.appbundler

import java.io._
import java.net.URL
import java.nio.file._
import java.util.zip.ZipInputStream

import scala.sys.process.Process

/** Port of Oracle's AppBundlerTask.java.
  *
  */
object AppBundler {
  val DEFAULT_ICON_NAME = "GenericApp.icns"
  val OS_TYPE_CODE = "APPL"

  val BUFFER_SIZE = 2048

  val runtimeIncludes = Seq(
    "jre/"
  )
  val runtimeExcludes = Seq(
    "bin/",
    "jre/bin/",
    "jre/lib/deploy/",
    "jre/lib/deploy.jar",
    "jre/lib/javaws.jar",
    "jre/lib/libdeploy.dylib",
    "jre/lib/libnpjp2.dylib",
    "jre/lib/plugin.jar",
    "jre/lib/security/javaws.policy"
  )
  val includePaths = runtimeIncludes map toPath
  val excludePaths = runtimeExcludes map toPath

  def toPath(p: String): Path = Paths.get(p)

  /**
    * Builds a .app package in the output directory.
    *
    * @param infoPlistConf app conf
    * @param dest          root dest path
    * @return path to DisplayName.app
    */
  def createBundle(infoPlistConf: InfoPlistConf, dest: Path): Path = {
    val conf = BundleStructure(infoPlistConf.displayName, dest)
    conf.prepare()
    PlistWriter.writeConf(infoPlistConf, conf.infoPlistFile)
    writePkgInfo(infoPlistConf.signature, conf.pkgInfoFile)
    copyExecutable(conf.macOSDir / infoPlistConf.executableName)
    copyResources(conf.resourcesDir)
    copyRuntime(infoPlistConf.javaHome, conf.pluginsDir)
    copyClassPath(infoPlistConf.jars, conf.javaDir)
    infoPlistConf.iconFile.fold(copyResourceSameName(DEFAULT_ICON_NAME, conf.resourcesDir))(p => {
      copy(p, conf.resourcesDir / p.getFileName)
    })
    conf.appDir
  }

  def copyExecutable(dest: Path) = {
    val exeFile = dest.toFile
    copyResource(exeFile.getName, dest)
    exeFile.setExecutable(true, false)
  }

  /** I do not understand what this is.
    *
    * @param dest resources destination
    */
  private def copyResources(dest: Path) = {
    val res = "res.zip"
    Option(getClass.getResourceAsStream(res)).foreach { stream =>
      using(new ZipInputStream(stream)) { zipInStream =>
        Iterator.continually(zipInStream.getNextEntry).takeWhile(_ != null).foreach { zipEntry =>
          val file = dest / zipEntry.getName
          if (zipEntry.isDirectory) {
            Files.createDirectories(file)
          } else {
            val outStream = new BufferedOutputStream(new FileOutputStream(file.toFile), BUFFER_SIZE)
            using(outStream) { str =>
              Iterator.continually(zipInStream.read()).takeWhile(_ != -1).foreach(b => outStream.write(b))
              outStream.flush()
            }
          }
        }
      }
    }
  }

  /**
    *
    * @param javaHome   the source: something like /Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home
    * @param plugInsDir destination
    * @return
    */
  def copyRuntime(javaHome: Path, plugInsDir: Path) = {
    val javaHomeDir = resolveJavaDirectory(javaHome)
    val javaContentsDir = javaHomeDir.getParent
    val javaDir = javaContentsDir.getParent

    val pluginDir = plugInsDir / javaDir.getFileName
    Files.createDirectories(pluginDir)

    val pluginContentsDir = pluginDir / javaContentsDir.getFileName
    Files.createDirectories(pluginContentsDir)

    val runtimeInfoPlistFile = javaContentsDir / "Info.plist"
    copy(runtimeInfoPlistFile, pluginContentsDir / runtimeInfoPlistFile.getFileName)

    val pluginHomeDir = pluginContentsDir / javaHomeDir.getFileName
    val conf = IncludeConf(javaHomeDir, pluginHomeDir, includePaths, excludePaths)
    Files.walkFileTree(conf.src, new Copier(conf))
  }

  /**
    * Converts `javaHome` to a jdk/Contents/Home path.
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

  def copyClassPath(jars: Seq[Path], dest: Path) =
    jars.foreach { jar =>
      copy(jar, dest / jar.getFileName)
    }

  def writePkgInfo(signature: String, dest: Path) = writerTo(dest)(_.println(s"$OS_TYPE_CODE$signature"))

  class Copier(conf: IncludeConf) extends IncludeExcludeVisitor(conf) {
    override def onSuccess(path: Path): Unit = {
      copy(path, dest / (src relativize path))
    }
  }

  /**
    * Copies `file` to `dest` if defined, otherwise copies the resource at `orElseResource` to `dest`.
    *
    * @param file           optional file
    * @param orElseResource fallback resource
    * @param dest           destination file
    */
  def copyFileOrResource(file: Option[Path], orElseResource: String, dest: Path) =
    file.fold(copyResource(orElseResource, dest))(f => copy(f, dest))

  def copyResourceSameName(resName: String, destDir: Path) =
    copyResource(resName, destDir / Paths.get(resName).getFileName)

  def copyResource(resName: String, dest: Path) = copy(resource(resName), dest)

  def copy(url: URL, dest: Path): Unit =
    using(url.openStream()) { stream =>
      Files.copy(stream, dest, StandardCopyOption.REPLACE_EXISTING)
    }

  def copy(source: Path, dest: Path): Unit = {
    Option(dest.getParent).foreach { d =>
      if (!Files.isDirectory(d)) {
        Files.createDirectories(d)
      }
    }
    Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS)
  }

  def delete(file: Path): Unit = {
    if (Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
      if (Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {
        file.toFile.listFiles().map(_.toPath).foreach(delete)
      }
      Files.delete(file)
    }
  }

  def resource(resource: String): URL = obtainResource(resource, _.getResource)

  def obtainResource[T](resource: String, getter: ClassLoader => String => T): T =
    Option(getter(getClass.getClassLoader)(resource))
      .getOrElse(throw new Exception(s"Unable to locate resource: '$resource'."))

  def writerTo(filename: Path)(op: PrintWriter => Unit): Unit =
    using(new PrintWriter(new BufferedWriter(new FileWriter(filename.toFile))))(op)

  def using[T <: AutoCloseable, U](resource: T)(op: T => U): U =
    try op(resource) finally resource.close()
}
