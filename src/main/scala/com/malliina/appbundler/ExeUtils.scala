package com.malliina.appbundler

import java.nio.file.Path

import org.slf4j.Logger

import scala.sys.process.{Process, ProcessBuilder}

object ExeUtils:
  def executeRedirected(cmd: Seq[String], redir: Path, logger: Logger): Unit =
    import scala.sys.process.*
    val processBuilder = cmd #> redir.toFile
    logged(cmd, processBuilder, logger)

  /** Executes the supplied command with the given parameters, logging the command and any
    * subsequent output using the logger's INFO level.
    *
    * @param cmd
    *   command to execute
    * @param logger
    *   the logger
    */
  def execute(cmd: Seq[String], logger: Logger): Unit = logged(cmd, Process(cmd), logger)

  private def logged(cmd: Seq[String], pb: => ProcessBuilder, logger: Logger): Unit =
    logger.info(cmd.mkString(" "))
    runLogged(pb, logger)

  private def runLogged(pb: ProcessBuilder, logger: Logger): Unit =
    val stream = pb.lazyLines
    stream.foreach(line => logger.info(line))
