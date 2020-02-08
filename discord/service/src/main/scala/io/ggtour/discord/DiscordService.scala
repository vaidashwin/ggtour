package io.ggtour.discord

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRefResolver, Behavior}
import io.ggtour.account.model.{Account, DiscordID}
import io.ggtour.account.service.AccountMessages.GetAccountByDiscordID
import io.ggtour.account.service.AccountServiceName
import io.ggtour.common.service.GGResponse
import io.ggtour.core.service.RemotingFacade._
import io.ggtour.core.service.{ServiceActor, ServiceNode}
import io.ggtour.discord.service.DiscordMessages._
import io.ggtour.discord.service.{DiscordMessage, DiscordServiceName}
import io.ggtour.ladder.service.LadderMessages.ChallengePlayer
import io.ggtour.ladder.service.LadderServiceName

object DiscordService extends ServiceNode(DiscordServiceActor) {
  val discordClient = DiscordBot(this)
}

object DiscordServiceActor
    extends ServiceActor[DiscordMessage](DiscordServiceName) {
  override def serviceBehavior: Behavior[DiscordMessage] = Behaviors.receivePartial {
    case (_, DMUser(user, message)) =>
      // TODO: impl
      Behaviors.stopped
    // Process client requests
    case (context, LfgFromDiscord(user)) =>
      implicit val actorSystem = context.system
      logger.debug("Player {} is looking for game.", user.username)
      getServiceActor(AccountServiceName) ! GetAccountByDiscordID(
        DiscordID(user.username, user.discriminator.toShort),
        (
          DiscordServiceName,
          ActorRefResolver(context.system).toSerializationFormat(context.self)
        )
      )
      Behaviors.receive {
        case (_, GGResponse(Some(account: Account))) =>
          getServiceActor(LadderServiceName) ! ChallengePlayer(
            account.accountID,
            Vector(),
            null,
            1)
          Behaviors.stopped
        case _ =>
          logger.warn("Challenger does not have an account.")
          Behaviors.stopped
      }
  }

}
