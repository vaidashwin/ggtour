package io.ggtour.account.service

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.ggtour.core.service._
import io.ggtour.core.service.RemotingFacade._
import AccountMessages._
import io.ggtour.account.model.Account
import io.ggtour.common.service.GGResponse

import scala.util.{Failure, Success}

object AccountService extends ServiceNode(AccountServiceActor)

object AccountServiceActor
    extends ServiceActor[AccountMessage](AccountServiceName) {
  override def serviceBehavior: Behavior[AccountMessage] = Behaviors.receivePartial {
    case (
        context,
        _ @GetAccountByDiscordID(discordID, (replyService, replyTo))) =>
      import context.executionContext
      implicit val actorSystem = context.system
      Account.forDiscordID(discordID).onComplete {
        case Success(accountOption) =>
          getActorForPath[GGResponse[Option[Account]]](replyTo, replyService) ! replyService
            .replyWith(accountOption)
        case Failure(exception) =>
          logger.error("Error while getting account object.", exception)
          getActorForPath[GGResponse[Option[Account]]](replyTo, replyService) ! replyService
            .replyWith(None)
      }
      Behaviors.stopped
  }
}
