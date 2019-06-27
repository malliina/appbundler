package com.malliina.appbundler

import java.nio.file.Path

import org.slf4j.Logger

import scala.sys.process.{Process, ProcessBuilder}

object ExeUtils {
  def executeRedirected(cmd: Seq[String], redir: Path, logger: Logger): Unit = {
    import scala.sys.process._
    val processBuilder = cmd #> redir.toFile
    logged(cmd, processBuilder, logger)
  }

  /** Executes the supplied command with the given parameters,
    * logging the command and any subsequent output using the logger's INFO level.
    *
    * @param cmd    command to execute
    * @param logger the logger
    */
  def execute(cmd: Seq[String], logger: Logger) = logged(cmd, Process(cmd), logger)

  def logged(cmd: Seq[String], pb: => ProcessBuilder, logger: Logger): Unit = {
    logger info cmd.mkString(" ")
    runLogged(pb, logger)
  }

  def runLogged(pb: ProcessBuilder, logger: Logger) = {
    val stream = pb.lineStream
    stream.foreach(line => logger.info(line))
  }
}
