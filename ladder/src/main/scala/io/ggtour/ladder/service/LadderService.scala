package io.ggtour.ladder.service

import java.util.UUID

import io.ggtour.core.service.{GGMessage, ServiceActor}
import LadderMessages._
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.ggtour.core.redis.GGRedis
import io.ggtour.ladder.challenge.Challenge
import spray.json._
import io.ggtour.ladder.json.LadderJsonFormats._

object LadderService extends ServiceActor {
  override def serviceBaseName: String = "ladder"
  override def serviceBehavior: Behavior[GGMessage] = Behaviors.receive {
    case (_, ChallengePlayer(challengerID, challengeeID, formatID, bestOf)) =>
      GGRedis.clients.withClient { client =>
        val challengeID = UUID.randomUUID()
        val challenge = Challenge(
          challengeID,
          challengerID,
          challengeeID,
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
        client.get(challengeID).map(_.parseJson).collect {
          case JsObject(Challenge(_, challengerID, challengeeID, formatID, bestOf, _, results)) =>
            val newChallenge = Challenge(challengeID,
              challengerID,
              challengeeID,
              formatID,
              bestOf,
              isAccepted = true,
              results
            )
            if ( newChallenge.isComplete ) {
              // TODO: write ELO changes
            } else {
              // update stored challenge
              client.set(challengeID, newChallenge.toJson)
            }
        }.getOrElse {
          // no existing challenge, so this was a pug
          // TODO: write ELO changes
        }
      }
      Behaviors.stopped
  }

}