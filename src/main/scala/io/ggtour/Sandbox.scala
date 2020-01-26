package main.scala.io.ggtour

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import io.ggtour.core.service.{GGMessage, ServiceClient}
import io.ggtour.discord.DiscordServiceActor
import io.ggtour.discord.service.DiscordMessage
import io.ggtour.ladder.service.{LadderMessage, LadderServiceActor}

object Sandbox extends App {
  // Add handlers here for any new services for sandbox testing.
  def behavior: Behavior[GGMessage] = Behaviors.receive {
    case (context, discordMessage: DiscordMessage) =>
      context.spawn(DiscordServiceActor.serviceBehavior, DiscordServiceActor.getActorName) ! discordMessage
      Behaviors.same
    case (context, ladderMessage: LadderMessage) =>
      context.spawn(LadderServiceActor.serviceBehavior, LadderServiceActor.getActorName) ! ladderMessage
      Behaviors.same
  }

  val actorSystem = ActorSystem(behavior, "ggtour-sandbox")
  ServiceClient.init(new SandboxServiceClient(actorSystem))
}