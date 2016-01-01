package com.malliina.appbundler

import java.nio.file._

import com.malliina.file.StorageFile

/**
 * @author Michael
 */
class IncludeExcludeCopier(conf: IncludeConf) extends IncludeExcludeVisitor(conf) {

  override def onSuccess(path: Path): Unit = {
    val pathDest = dest / (src relativize path)
    Files.copy(path, pathDest, StandardCopyOption.REPLACE_EXISTING)
  }
}

