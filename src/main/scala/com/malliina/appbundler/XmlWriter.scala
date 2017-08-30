package com.malliina.appbundler

import java.nio.file.Path

import com.malliina.appbundler.XmlWriter.log
import org.slf4j.LoggerFactory

import scala.xml.{Node, PrettyPrinter}

trait XmlWriter {
  def prefix: String = decl()

  def decl(enc: String = "UTF-8") = s"<?xml version='1.0' encoding='$enc'?>\n"

  def writePretty(xml: Node, dest: Path): Unit = AppBundler.writerTo(dest) { w =>
    w.println(stringify(xml))
    log info s"Wrote $dest"
  }

  def stringify(xml: Node): String = {
    val printer = new PrettyPrinter(1000, 2)
    val payload = printer format xml
    prefix + payload
  }
}

object XmlWriter {
  private val log = LoggerFactory.getLogger(getClass.getName.stripSuffix("$"))
}
