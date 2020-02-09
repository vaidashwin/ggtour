package io.ggtour.discord

import ackcord._
import ackcord.data.{GuildChannel, User}
import ackcord.requests._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.stream.scaladsl.Sink
import io.ggtour.account.model.DiscordID
import io.ggtour.core.service.ServiceNode
import io.ggtour.common.config.GGTourConfig
import io.ggtour.common.service.GGMessage
import io.ggtour.core.service.ServiceNode

case class DiscordBot(service: ServiceNode[_ <: GGMessage]) {
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
    .to(Sink.foreach(_ => println("I have returned.")))
    .run()

  val gatewaySettings = GatewaySettings(token)
  DiscordShard.fetchWsGateway.foreach { wsUri =>
    val shard = actorSystem.systemActorOf(
      DiscordShard(wsUri, gatewaySettings, cache),
      "discord:shard")
    shard ! DiscordShard.StartShard
  }

  val commands: BotCommands = new BotCommands(cache, requests, actorSystem)

  def tellUser(user: User, message: String, inChannel: GuildChannel): Unit = {
    requests.singleIgnore(CreateMessage(inChannel.id, CreateMessageData(content = s"${user.mentionNick}: $message")))
  }
}
