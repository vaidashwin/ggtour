package io.ggtour.discord

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRefResolver, ActorSystem, Behavior}
import io.ggtour.account.model.{Account, DiscordID}
import io.ggtour.account.service.AccountMessages.GetAccountByDiscordID
import io.ggtour.account.service.AccountServiceName
import io.ggtour.common.service.GGResponse
import io.ggtour.core.service.RemotingFacade._
import io.ggtour.core.service.{ServiceActor, ServiceNode}
import io.ggtour.discord.service.DiscordMessages._
import io.ggtour.discord.service.{DiscordMessage, DiscordServiceName}
import io.ggtour.ladder.formats.LadderFormats
import io.ggtour.ladder.service.LadderMessages.{ChallengePlayer, RespondToChallenge}
import io.ggtour.ladder.service.{LadderMessages, LadderServiceName}

object DiscordService extends ServiceNode(DiscordServiceActor) {
  val discordClient = DiscordBot(this)
  def getClient = discordClient
}

object DiscordServiceActor extends ServiceActor[DiscordMessage](DiscordServiceName) {
  override def serviceBehavior: Behavior[DiscordMessage] =
    Behaviors.receivePartial {
      case (_, DMUser(user, message)) =>
        // TODO: impl
        Behaviors.stopped
      // Process client requests
      case (context, LfgFromDiscord(user, inChannel)) =>
        implicit val actorSystem = context.system
        logger.debug("Player {} is looking for game.", user.username)
        getServiceActor(AccountServiceName) ! GetAccountByDiscordID(
          DiscordID(user.username, user.discriminator.toShort),
          context.self
        )
        Behaviors.receive {
          case (_, GGResponse(Some(account: Account))) =>
            LadderFormats(inChannel.guildId) match {
              case Some(fmt) =>
                getServiceActor(LadderServiceName) ! ChallengePlayer(account.accountID, Vector(), fmt.id, 1)
                DiscordService.getClient.tellUser(user, "Challenge created.", inChannel)
                Behaviors.stopped
              case _ =>
                DiscordService.getClient.tellUser(user, "Can't create a challenge from this channel.", inChannel)
                Behaviors.stopped
            }
          case _ =>
            logger.warn("Challenger does not have an account.")
            DiscordService.getClient.tellUser(
              user,
              "Couldn't find your account; please register at https://ggtour.io/.",
              inChannel)
            Behaviors.stopped
        }
      case (context, RespondToChallengeFromDiscord(responder, challenger, accepted)) =>
        implicit val actorSystem = context.system
        getServiceActor(AccountServiceName) ! GetAccountByDiscordID(
          DiscordID(responder.username, responder.discriminator.toShort),
          context.self
        )
        // todo: refactor repeated pattern
        Behaviors.receiveMessagePartial {
          case GGResponse(Some(responderAccount: Account)) =>
            getServiceActor(AccountServiceName) ! GetAccountByDiscordID(
              DiscordID(responder.username, challenger.discriminator.toShort),
              context.self
            )
            Behaviors.receiveMessagePartial {
              case GGResponse(Some(targetAccount: Account)) =>
                getServiceActor(LadderServiceName) ! RespondToChallenge(
                  responderAccount.accountID,
                  targetAccount.accountID,
                  accepted,
                  context.self
                )
                Behaviors.receiveMessagePartial {
                  case GGResponse(Some(true)) =>
                    DiscordService.getClient.dmUser(
                      challenger,
                      s"Your challenge was accepted by ${responder.mentionNick}! You have 24 hours to play.")
                    Behaviors.stopped
                  case GGResponse(Some(false)) =>
                    DiscordService.getClient
                      .dmUser(challenger, s"Your challenge was rejected by ${responder.mentionNick}.")
                    Behaviors.stopped
                }
            }
        }
    }

}
