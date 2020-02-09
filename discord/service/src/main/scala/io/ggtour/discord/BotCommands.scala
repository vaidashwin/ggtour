package io.ggtour.discord

import ackcord._
import ackcord.commands._
import ackcord.syntax._
import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import io.ggtour.discord.service.DiscordMessages._
import io.ggtour.core.service.RemotingFacade._
import io.ggtour.discord.service.DiscordServiceName

class BotCommands(
    cache: Cache,
    requestHelper: RequestHelper,
    implicit val actorSystem: ActorSystem[_]) {
  implicit private def optionToList[T](option: Option[T]): List[T] =
    option.toList

  val noPrefix = ""
  val commands: Commands = CoreCommands.create(
    CommandSettings(needsMention = true, Set()),
    cache,
    requestHelper)

  def lfgRaw: Sink[RawCmdMessage, NotUsed] =
    Flow[RawCmdMessage]
      .collect {
        case NoCmdPrefix(msg, "lfg", Nil, c) =>
          implicit val cache: CacheSnapshot = c
          Source(
            for {
              channel <- msg.tGuildChannel
              user <- msg.authorUser
            } yield {
              getServiceActor(DiscordServiceName) ! LfgFromDiscord(user, channel)
              channel.triggerTyping
            }
          )
      }
      .flatMapConcat(identity)
      .to(requestHelper.sinkIgnore)

  commands.subscribeRaw.to(lfgRaw).run()
}
