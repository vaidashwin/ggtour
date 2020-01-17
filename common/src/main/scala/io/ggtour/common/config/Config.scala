package io.ggtour.common.config

import com.typesafe.config.{Config => TSConfig, ConfigFactory}

object Config {
  private val config = ConfigFactory.load()
  def apply(): Config = config
}
