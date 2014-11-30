package com.mle.appbundler

import java.nio.file._

import com.mle.file.StorageFile

/**
 * @author Michael
 */
class IncludeExcludeCopier(conf: IncludeConf) extends IncludeExcludeVisitor(conf) {

  override def onSuccess(path: Path): Unit = {
    val pathDest = dest / (src relativize path)
    Files.copy(path, pathDest, StandardCopyOption.REPLACE_EXISTING)
  }
}

