package com.mle.appbundler

import org.slf4j.Logger

import scala.sys.process.Process


/**
 * @author Michael
 */
object ExeUtils {
  /**
   * Executes the supplied command with the given parameters,
   * logging the command and any subsequent output using the logger's INFO level.
   *
   * @param cmd command to execute
   * @param logger the logger
   */
  def execute(cmd: Seq[String], logger: Logger) {
    val output = execute2(cmd, logger)
    output.foreach(line => logger.info(line))
  }

  /**
   * Executes the supplied command, logging only the command executed.
   *
   * @param cmd
   * @param logger
   * @return all output lines up to termination
   */
  def execute2(cmd: Seq[String], logger: Logger): Stream[String] = {
    logger.info(cmd.mkString(" "))
    Process(cmd.head, cmd.tail).lines
  }
}

