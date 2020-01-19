package io.ggtour.discord

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.ggtour.core.service.{GGMessage, ServiceActor}
import io.ggtour.discord.service.DiscordMessages.DMUser

object DiscordService extends ServiceActor {
  override protected def serviceBaseName: String = "discord"

  override def serviceBehavior: Behavior[GGMessage] = Behaviors.receive {
    case (context, DMUser(user, message)) =>
      Behaviors.stopped
  }

}
