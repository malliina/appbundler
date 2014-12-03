package com.mle.appbundler

import java.nio.file.Path

import com.mle.file.FileUtilities

import scala.xml.dtd.{DocType, PublicID}
import scala.xml._

/**
 * Do not format this document with IntelliJ IDEA.
 *
 * @author Michael
 */
object PlistWriter extends XmlWriter{
//  val PLIST_DTD = "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"
  val docType = DocType("plist", PublicID("-//Apple//DTD PLIST 1.0//EN", "http://www.apple.com/DTDs/PropertyList-1.0.dtd"), Nil)

  def writeConf(conf: InfoPlistConf, dest: Path) =
    writePretty(confXml(conf), dest)

  def writeDaemon(appIdentifier: String, displayName: String, dest: Path) =
    writePretty(daemonXml(appIdentifier, displayName), dest)

  override def prefix: String = decl() + docTypeString(docType)

  def daemonXml(appIdentifier: String, displayName: String) = plistXml {
    <key>Label</key>
    <string>{appIdentifier}</string>
    <key>ProgramArguments</key>
    <array>
      <string>/Applications/{displayName}.app/Contents/MacOS/JavaAppLauncher</string>
    </array>
    <key>KeepAlive</key>
    <true/>
    <key>RunAtLoad</key>
    <true/>
  }

  def docTypeString(docType: DocType) = s"$docType\n"

  def confXml(conf: InfoPlistConf): Node = plistXml {
    conf.singles.map(p => toProperty(p._1, p._2)) ++ conf.arrays.map(a => toArray(a._1, a._2))
  }

  def plistXml(dictContent: Iterable[NodeSeq]): Node = {
    <plist version="1.0">
      <dict>
        {dictContent}
      </dict>
    </plist>
  }

  def toProperty(key: String, value: String): NodeSeq = {
    <key>{key}</key>
    <string>{value}</string>
  }

  def toArray(key: String, values: Seq[String]): NodeSeq = {
    <key>{key}</key>
    <array>
      {values.map(toValue)}
    </array>
  }
  def toValue(value: String): NodeSeq = {
    <string>{value}</string>
  }
}
