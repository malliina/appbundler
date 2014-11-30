package com.mle.appbundler

import java.nio.file.Path

import com.mle.file.FileUtilities

import scala.xml.dtd.{DocType, PublicID}
import scala.xml.{Node, PrettyPrinter, XML}

/**
 * Do not format this document with IntelliJ IDEA.
 *
 * @author Michael
 */
object PlistWriter {
//  val PLIST_DTD = "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"
  val docType = DocType("plist", PublicID("-//Apple//DTD PLIST 1.0//EN", "http://www.apple.com/DTDs/PropertyList-1.0.dtd"), Nil)

  def write(conf: InfoPlistConf, dest: Path) = {
    FileUtilities.writerTo(dest)(_.println(stringify(conf)))
  }

  def stringify(conf: InfoPlistConf): String = {
    val xml = plistXml(conf)
    val printer = new PrettyPrinter(1000, 2)
    val payload = printer format xml
    decl() + docTypeString(docType) + payload
  }

  def decl(enc: String= "UTF-8") = s"<?xml version='1.0' encoding='$enc'?>\n"

  def docTypeString(docType:DocType) = s"$docType\n"

  def savePlist(xml: Node, file:Path) = {
    XML.save(file.toString, xml, "UTF-8", xmlDecl = true, docType)
  }

  def plistXml(conf: InfoPlistConf): Node = {
    <plist version="1.0">
      <dict>
        {conf.singles.map(p => toProperty(p._1, p._2))}
        {conf.arrays.map(a => toArray(a._1, a._2))}
      </dict>
    </plist>
  }

  def toProperty(key: String, value: String) = {
    <key>{key}</key>
    <string>{value}</string>
  }

  def toArray(key: String, values: Seq[String]) = {
    <key>{key}</key>
    <array>
      {values.map(toValue)}
    </array>
  }
  def toValue(value: String) = {
    <string>{value}</string>
  }
}
