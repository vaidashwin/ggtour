package io.ggtour.discord

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.ggtour.core.service.{ClusterNode, GGMessage, ServiceActor}
import io.ggtour.discord.service.DiscordMessage
import io.ggtour.discord.service.DiscordMessages._

object DiscordService extends ClusterNode(DiscordServiceActor)
object DiscordServiceActor extends ServiceActor[DiscordMessage] {
  override def serviceBaseName: String = "discord"

  override def serviceBehavior: Behavior[DiscordMessage] = Behaviors.receive {
    case (_, DMUser(user, message)) =>
      // TODO: impl
      Behaviors.stopped
  }

}
