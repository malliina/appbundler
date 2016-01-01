package com.malliina.appbundler

import java.nio.file.Path

/**
  * @author mle
  */
case class IncludeConf(src: Path, dest: Path, include: Seq[Path], exclude: Seq[Path])
