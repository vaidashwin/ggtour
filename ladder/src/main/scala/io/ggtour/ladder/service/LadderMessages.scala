package io.ggtour.ladder.service

import java.util.UUID

import io.ggtour.common.service.{GGMessage, GGRequest, GGResponse, ServiceName}
import io.ggtour.game.replay.Replay

object LadderMessages {
  case class ChallengePlayer(challengerID: UUID, challengeeIDs: Vector[UUID], formatID: UUID, bestOf: Int)
      extends LadderMessage {
    require(bestOf % 2 == 1 && bestOf > 0) // Must be an odd number of matches
  }
  case class RespondToChallenge(
      responderID: UUID,
      challengeID: UUID,
      accepted: Boolean,
      replyTo: (ServiceName[_ <: GGMessage], String))
      extends LadderRequest[Option[Boolean]]
  case class ReportResults(challengeID: Option[UUID], replay: Replay) extends LadderMessage
  case class CancelChallenge(challengerID: UUID) extends LadderMessage
}

sealed trait LadderMessage extends GGMessage
sealed trait LadderRequest[T] extends GGRequest[T] with LadderMessage
case class LadderResponse[T](override val payload: T) extends GGResponse(payload) with LadderMessage
