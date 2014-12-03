package com.mle.appbundler

import java.nio.file.Path

import com.mle.file.FileUtilities

import scala.xml.{Node, PrettyPrinter}

/**
 * @author Michael
 */
trait XmlWriter {
  def prefix: String = decl()

  def decl(enc: String = "UTF-8") = s"<?xml version='1.0' encoding='$enc'?>\n"

  def writePretty(xml: Node, dest: Path) = FileUtilities.writerTo(dest)(_.println(stringify(xml)))

  def stringify(xml: Node): String = {
    val printer = new PrettyPrinter(1000, 2)
    val payload = printer format xml
    prefix + payload
  }
}
