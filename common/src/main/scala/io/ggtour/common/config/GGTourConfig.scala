package io.ggtour.common.config

import com.typesafe.config.{Config, ConfigFactory}

object GGTourConfig {
  private val config = ConfigFactory.load()
  def apply(): Config = config
}
