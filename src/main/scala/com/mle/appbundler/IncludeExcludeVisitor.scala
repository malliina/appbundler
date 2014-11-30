package com.mle.appbundler

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Path, SimpleFileVisitor}

/**
  * @author mle
  */
abstract class IncludeExcludeVisitor(conf: IncludeConf) extends SimpleFileVisitor[Path] {
   val src = conf.src
   val dest = conf.dest
   val include = conf.include
   val exclude = conf.exclude

   def onSuccess(path: Path): Unit

   def qualifies(path: Path): Boolean = {
     val relDir = src relativize path
     path.toString == src.toString || (include.exists(relDir.startsWith) && !exclude.exists(relDir.startsWith))
   }

   override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
     if (qualifies(file)) {
       onSuccess(file)
     }
     FileVisitResult.CONTINUE
   }

   override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
     if (qualifies(dir)) {
       onSuccess(dir)
       FileVisitResult.CONTINUE
     } else {
       FileVisitResult.SKIP_SUBTREE
     }
   }
 }
