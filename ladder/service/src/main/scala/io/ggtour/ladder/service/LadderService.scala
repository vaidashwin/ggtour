package io.ggtour.ladder.service

import java.util.UUID

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.ggtour.account.model.Account
import io.ggtour.account.service.AccountMessages.{GetAccount, GetAccountByDiscordID}
import io.ggtour.account.service.AccountServiceName
import io.ggtour.common.service.GGResponse
import io.ggtour.core.redis.GGRedisClientPool
import io.ggtour.core.service.{ServiceActor, ServiceNode}
import io.ggtour.ladder.challenge.Challenge
import io.ggtour.ladder.elo.{GameResult, Result}
import io.ggtour.ladder.json.LadderJsonFormats._
import io.ggtour.ladder.service.LadderMessages._
import spray.json._
import io.ggtour.core.service.RemotingFacade._
import io.ggtour.discord.service.DiscordMessages.DMUser
import io.ggtour.discord.service.DiscordServiceName

import scala.concurrent.duration._

object LadderService extends ServiceNode(LadderServiceActor)

object LadderServiceActor extends ServiceActor[LadderMessage](LadderServiceName) {
  override def serviceBehavior: Behavior[LadderMessage] =
    Behaviors.receivePartial {
      case (_, ChallengePlayer(challengerID, challengeeIDs, formatID, bestOf)) =>
        GGRedisClientPool().withClient { client =>
          val challenge = Challenge(
            challengerID,
            challengeeIDs,
            formatID,
            bestOf,
            isAccepted = false,
            Vector()
          )
          client.set(challengerID, challenge.toJson, expire = 10 minutes)
        }
        Behaviors.stopped
      case (context, RespondToChallenge(responderID, challengerID, accepted: Boolean, (respondService, respondPath))) =>
        implicit val actorSystem = context.system
        GGRedisClientPool().withClient { client =>
          client.get(challengerID).map(_.parseJson.convertTo[Challenge]) match {
            case Some(challenge: Challenge) if !accepted && challenge.challengeeIDs.contains(responderID) =>
              logger.debug("Challenge rejected.")
              getActorForPath(respondPath, respondService) ! respondService.replyWith(Some(false))
              Behaviors.stopped
            case Some(challenge: Challenge) if !challenge.isAccepted && accepted =>
              logger.debug("Challenge accepted, should now be being played in the next day.")
              client.set(challengerID, challenge.copy(isAccepted = true), expire = 1 day)
              getActorForPath(respondPath, respondService) ! respondService.replyWith(Some(true))
              Behaviors.stopped
            case invalid =>
              logger.debug(s"Invalid response: $invalid")
              getActorForPath(respondPath, respondService) ! respondService.replyWith(None)
              Behaviors.stopped
          }
        }
      case (_, ReportResults(Some(challengerID), replay)) =>
        val winners = replay.getWinners
        GGRedisClientPool().withClient { client =>
          val newChallenge =
            client.get(challengerID).map(_.parseJson.convertTo[Challenge]).map {
              case Challenge(challengerID, challengeeIDs, formatID, bestOf, _, results) =>
                val losers = (challengerID +: challengeeIDs).diff(winners)
                Challenge(
                  challengerID,
                  challengeeIDs,
                  formatID,
                  bestOf,
                  isAccepted = true,
                  results :+ GameResult((winners.map(_ -> Result.Win) ++ losers
                    .map(_ -> Result.Loss)).toMap)
                )
            }
          if (newChallenge.exists(_.isComplete)) {
            client.del(challengerID) // challenge is over
            // TODO: write ELO changes
          } else {
            // update stored challenge
            newChallenge.map(chall => client.set(challengerID, chall.toJson))
          }
        }
        Behaviors.stopped
      case (_, CancelChallenge(challengerID)) =>
        logger.debug("Challenge being cancelled.")
        GGRedisClientPool().withClient(_.del(challengerID))
        Behaviors.stopped
    }

}
