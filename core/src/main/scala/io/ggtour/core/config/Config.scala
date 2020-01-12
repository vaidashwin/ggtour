package io.ggtour.core.config

import com.typesafe.config.{Config, ConfigFactory}

object Config {
  private val config = ConfigFactory.load()
  def apply(): Config = config
}
