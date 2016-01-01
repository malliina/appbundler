package com.malliina.appbundler

import java.nio.file.Path

import scala.xml._
import scala.xml.dtd.{DocType, PublicID}

/**
 * Do not format this document with IntelliJ IDEA.
 *
 * @author Michael
 */
object PlistWriter extends XmlWriter {
//  val PLIST_DTD = "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"
  val docType = DocType("plist", PublicID("-//Apple//DTD PLIST 1.0//EN", "http://www.apple.com/DTDs/PropertyList-1.0.dtd"), Nil)

  def writeConf(conf: InfoPlistConf, dest: Path) = writePretty(confXml(conf), dest)

//  def writeDaemon(appIdentifier: String, displayName: String, dest: Path) =
//    writePretty(daemonXml(appIdentifier, displayName), dest)

  override def prefix: String = decl() + docTypeString(docType)

//  def daemonXml(appIdentifier: String, displayName: String) = plistXml {
//    <key>Label</key>
//    <string>{appIdentifier}</string>
//    <key>ProgramArguments</key>
//    <array>
//      <string>/Applications/{displayName}.app/Contents/MacOS/JavaAppLauncher</string>
//    </array>
//    <key>KeepAlive</key>
//    <true/>
//    <key>RunAtLoad</key>
//    <true/>
//  }

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
  def toBool(key: String, value: Boolean) = {
    <key>{key}</key> ++ toBoolean(value)
  }
  def toBoolean(bool: Boolean) =
    if(bool) <true/>
    else <false/>

  def dictionary(key: String, kvs: Map[String,String]) = {
    <key>{key}</key>
    <dict>
      {kvs.map(p => toProperty(p._1, p._2))}
    </dict>
  }
  def booleanDict(key: String, bool: Boolean) = {
    <dict>
      <key>{key}</key>
      {toBoolean(bool)}
    </dict>
  }

  def optionalProperty(key: String, value: Option[String]) = {
    value.fold(NodeSeq.Empty)(toProperty(key, _))
  }
}
