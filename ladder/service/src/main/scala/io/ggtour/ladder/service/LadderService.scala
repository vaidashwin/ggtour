package io.ggtour.ladder.service

import java.util.UUID

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.ggtour.core.redis.GGRedis
import io.ggtour.core.service.{ClusterNode, GGMessage, ServiceActor}
import io.ggtour.ladder.challenge.Challenge
import io.ggtour.ladder.elo.{GameResult, Result}
import io.ggtour.ladder.json.LadderJsonFormats._
import io.ggtour.ladder.service.LadderMessages._
import spray.json._

object LadderService extends ClusterNode(LadderServiceActor)
object LadderServiceActor extends ServiceActor {
  override def serviceBaseName: String = "ladder"
  override def serviceBehavior: Behavior[GGMessage] = Behaviors.receive {
    case (_, ChallengePlayer(challengerID, challengeeIDs, formatID, bestOf)) =>
      GGRedis.clients.withClient { client =>
        val challengeID = UUID.randomUUID()
        val challenge = Challenge(
          challengeID,
          challengerID,
          challengeeIDs,
          formatID,
          bestOf,
          isAccepted = false,
          Vector()
        )
        // TODO: message discord service to ping the player
        client.set(challengeID, challenge.toJson)
      }
      Behaviors.stopped
    case (_, ReportResults(Some(challengeID), replay)) =>
      val winners = replay.getWinners
      GGRedis.clients.withClient { client =>
        val newChallenge =
          client.get(challengeID).map(_.parseJson.convertTo[Challenge]).map {
            case Challenge(
                _,
                challengerID,
                challengeeIDs,
                formatID,
                bestOf,
                _,
                results) =>
              val losers = (challengerID +: challengeeIDs).diff(winners)
              Challenge(
                challengeID,
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
          client.del(challengeID) // challenge is over
          // TODO: write ELO changes
        } else {
          // update stored challenge
          newChallenge.map(chall => client.set(challengeID, chall.toJson))
        }
      }
      Behaviors.stopped
  }

}
