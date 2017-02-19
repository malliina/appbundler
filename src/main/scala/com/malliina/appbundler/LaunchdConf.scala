package com.malliina.appbundler

import java.nio.file.{Path, Paths}

import com.malliina.appbundler.LaunchdConf.{DEFAULT_PLIST_DIR, KeepAliveOption, OnDemand}
import com.malliina.appbundler.PlistWriter.{booleanDict, dictionary, optionalProperty, plistXml, toArray, toBool, toProperty}

import scala.xml.Node

case class LaunchdConf(label: String,
                       programArguments: Seq[String],
                       keepAlive: KeepAliveOption = OnDemand,
                       runAtLoad: Boolean = true,
                       user: Option[String] = None,
                       group: Option[String] = None,
                       rootDirectory: Option[String] = None,
                       workingDirectory: Option[Path] = None,
                       environmentVariables: Map[String, String] = Map.empty,
                       standardOut: Option[Path] = None,
                       standardError: Option[Path] = None,
                       plistDir: Path = DEFAULT_PLIST_DIR) {

  lazy val xml = plistXml {
    toProperty("Label", label) ++
      toArray("ProgramArguments", programArguments) ++
      keepAlive.toXml ++
      toBool("RunAtLoad", runAtLoad || keepAlive.requiresRunAtLoad) ++
      optionalProperty("UserName", user) ++
      optionalProperty("GroupName", group) ++
      optionalProperty("RootDirectory", rootDirectory) ++
      optionalProperty("WorkingDirectory", workingDirectory.map(_.toString)) ++
      dictionary("EnvironmentVariables", environmentVariables) ++
      optionalProperty("StandardOutPath", standardOut.map(_.toString)) ++
      optionalProperty("StandardErrorPath", standardError.map(_.toString))
  }

  def stringify = PlistWriter.stringify(xml)

  def write(dest: Path) = PlistWriter.writePretty(xml, dest)
}

object LaunchdConf {
  val DEFAULT_PLIST_DIR = Paths get "/Library/LaunchAgents"

  def executable(displayName: String) = s"/Applications/$displayName.app/Contents/MacOS/JavaAppLauncher"

  def defaultSettings(displayName: String, appIdentifier: String) = {
    LaunchdConf(appIdentifier, Seq(executable(displayName)))
  }

  trait KeepAliveOption {

    def requiresRunAtLoad: Boolean = false

    def toXml = <key>KeepAlive</key> ++ xml

    protected def xml: Node
  }

  case class SuccessfulExit(restartOnZeroExit: Boolean = true) extends KeepAliveOption {
    override protected def xml: Node = booleanDict("SuccessfulExit", restartOnZeroExit)

    override def requiresRunAtLoad: Boolean = true
  }

  case class NetworkState(keepAliveIfUp: Boolean = true) extends KeepAliveOption {
    override protected def xml: Node = booleanDict("NetworkState", keepAliveIfUp)
  }

  case object Always extends KeepAliveOption {
    override protected def xml: Node = <true/>
  }

  case object OnDemand extends KeepAliveOption {
    override protected def xml: Node = <false/>
  }

}