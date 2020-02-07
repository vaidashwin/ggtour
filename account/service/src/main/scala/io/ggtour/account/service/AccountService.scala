package io.ggtour.account.service

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.ggtour.core.service._
import io.ggtour.core.service.RemotingFacade._
import AccountMessages._
import io.ggtour.account.model.Account

import scala.util.{Failure, Success}

object AccountService extends ServiceNode(AccountServiceActor)

object AccountServiceActor extends ServiceActor[AccountMessage](AccountServiceName) {
  override def serviceBehavior: Behavior[AccountMessage] = Behaviors.receive {
    case (context, _ @ GetAccountByDiscordID(discordID, (replyService, replyTo))) =>
      import context.executionContext
      implicit val actorSystem = context.system
      Account.forDiscordID(discordID).onComplete {
        case Success(accountOption) =>
          getActorForPath[Option[Account]](replyTo, replyService) ! accountOption
        case Failure(exception) =>
          logger.error("Error while getting account object.", exception)
          getActorForPath[Option[Account]](replyTo, replyService) ! None
      }
    Behaviors.stopped
  }
}
