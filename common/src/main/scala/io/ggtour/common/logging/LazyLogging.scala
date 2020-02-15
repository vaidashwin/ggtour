package io.ggtour.common.logging

import org.slf4j.{Logger, LoggerFactory}

trait LazyLogging {
  lazy val logger: Logger = LoggerFactory.getLogger(getClass)
}
