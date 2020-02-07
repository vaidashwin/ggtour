package io.ggtour.core.redis

import com.redis.RedisClientPool
import io.ggtour.common.config.GGTourConfig

object GGRedisClientPool {
  lazy val clientPool = new RedisClientPool(
    host = GGTourConfig().getString("redis.host"),
    port = GGTourConfig().getInt("redis.port"),
    secret = Some(GGTourConfig().getString("redis.password"))
  )
  def apply(): RedisClientPool = clientPool
}
