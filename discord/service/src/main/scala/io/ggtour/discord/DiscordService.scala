package io.ggtour.discord

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.ggtour.core.service.{ClusterNode, GGMessage, ServiceActor}
import io.ggtour.discord.service.DiscordMessages.DMUser

object DiscordService extends ClusterNode(DiscordServiceActor)

object DiscordServiceActor extends ServiceActor {
  override protected def serviceBaseName: String = "discord"

  override def serviceBehavior: Behavior[GGMessage] = Behaviors.receive {
    case (_, DMUser(user, message)) =>
      // TODO: impl
      Behaviors.stopped
  }

}
