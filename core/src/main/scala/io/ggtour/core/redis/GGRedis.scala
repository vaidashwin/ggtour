package io.ggtour.core.redis

import com.redis.RedisClientPool

object GGRedis {
  val clients = new RedisClientPool("localhost", 6379)

}
