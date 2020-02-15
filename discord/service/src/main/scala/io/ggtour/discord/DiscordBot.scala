package io.ggtour.discord

import ackcord._
import ackcord.data.{DMChannel, GuildChannel, User}
import ackcord.requests._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.stream.scaladsl.Sink
import io.ggtour.account.model.DiscordID
import io.ggtour.core.service.ServiceNode
import io.ggtour.common.config.GGTourConfig
import io.ggtour.common.logging.LazyLogging
import io.ggtour.common.service.GGMessage
import io.ggtour.core.service.ServiceNode

import scala.util.Success

case class DiscordBot(service: ServiceNode[_ <: GGMessage]) extends LazyLogging {
  implicit val actorSystem: ActorSystem[_] = service.actorSystem

  import actorSystem.executionContext
  val token: String = GGTourConfig().getString("discord.token")

  val cache: Cache = Cache.create
  val rateLimiter: ActorRef[Ratelimiter.Command] =
    actorSystem.systemActorOf(Ratelimiter(), "discord:ratelimiter")
  val requests =
    new RequestHelper(ackcord.requests.BotAuthentication(token), rateLimiter)

  // Listen for Discord events
  cache.subscribeAPI
    .collect {
      case APIMessage.Ready(c) => c
    }
    .to(Sink.foreach(_ => logger.info("I have returned.")))
    .run()

  val gatewaySettings = GatewaySettings(token)
  DiscordShard.fetchWsGateway.foreach { wsUri =>
    val shard = actorSystem.systemActorOf(DiscordShard(wsUri, gatewaySettings, cache), "discord:shard")
    shard ! DiscordShard.StartShard
  }

  val commands: BotCommands = new BotCommands(cache, requests, actorSystem)

  def tellUser(user: User, message: String, inChannel: GuildChannel): Unit =
    requests.singleIgnore(CreateMessage(inChannel.id, CreateMessageData(content = s"${user.mentionNick}: $message")))
  def dmUser(user: User, message: String): Unit =
    requests.singleFuture(CreateDm(CreateDMData(user.id))).onComplete {
      case Success(RequestResponse(rawChannel, _, _, _)) => rawChannel.toChannel.foreach { channel =>
        requests.singleIgnore(CreateMessage(channel.id, CreateMessageData(content = message)))
      }
      case _ => logger.error(s"Failed to DM user $user")
    }
}
