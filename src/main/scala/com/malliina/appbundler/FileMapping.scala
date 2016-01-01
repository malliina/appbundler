package com.malliina.appbundler

import java.nio.file.Path

/**
 * @author mle
 */
case class FileMapping(before: Path, after: Path)

object FileMapping {
  def sameName(name: Path) = FileMapping(name, name)
}
