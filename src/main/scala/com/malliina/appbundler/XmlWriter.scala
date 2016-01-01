package com.malliina.appbundler

import java.nio.file.Path

import com.malliina.file.FileUtilities
import com.malliina.util.Log

import scala.xml.{Node, PrettyPrinter}

/**
 * @author Michael
 */
trait XmlWriter extends Log{
  def prefix: String = decl()

  def decl(enc: String = "UTF-8") = s"<?xml version='1.0' encoding='$enc'?>\n"

  def writePretty(xml: Node, dest: Path) = FileUtilities.writerTo(dest)(w => {
    w.println(stringify(xml))
    log info s"Wrote $dest"
  })

  def stringify(xml: Node): String = {
    val printer = new PrettyPrinter(1000, 2)
    val payload = printer format xml
    prefix + payload
  }
}
