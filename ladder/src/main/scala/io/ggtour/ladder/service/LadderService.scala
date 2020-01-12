package io.ggtour.ladder.service

import java.util.UUID

import io.ggtour.core.service.{GGMessage, ServiceActor}
import LadderMessages._
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.ggtour.core.redis.GGRedis
import io.ggtour.ladder.challenge.Challenge

object LadderService extends ServiceActor {
  override def serviceBaseName: String = "ladder"
  override def serviceBehavior: Behavior[GGMessage] = Behaviors.receive {
    case (_, ChallengePlayer(challengerID, challengeeID, formatID, bestOf)) =>
      GGRedis.clients.withClient { client =>
        val challengeUUID = UUID.randomUUID()
        val challenge = Challenge(
          challengeUUID,
          challengerID,
          challengeeID,
          formatID,
          bestOf,
          isAccepted = false,
          Vector()
        )
        // TODO: message discord service to ping the player
        client.set(challengeUUID, challenge)
      }
      Behaviors.stopped
    case (_, ReportResults(Some(challengeID), replay)) =>
      GGRedis.clients.withClient { client =>
        client.get(challengeID)

      }
      Behaviors.stopped
  }

}