package com.mle.appbundler

import java.nio.file.Path

import com.mle.appbundler.LaunchdConf.{KeepAliveOption, OnDemand}
import com.mle.appbundler.PlistWriter._

import scala.xml.Node


/**
 * @author Michael
 */
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
                       standardError: Option[Path] = None) {
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

  def defaultSettings(displayName: String, appIdentifier: String) = {
    val argument = s"/Applications/$displayName.app/Contents/MacOS/JavaAppLauncher"
    LaunchdConf(appIdentifier, Seq(argument))
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