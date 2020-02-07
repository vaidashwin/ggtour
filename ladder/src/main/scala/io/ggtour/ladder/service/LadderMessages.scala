package io.ggtour.ladder.service

import java.util.UUID

import io.ggtour.common.service.GGMessage
import io.ggtour.game.replay.Replay

object LadderMessages {
  case class ChallengePlayer(
      challengerID: UUID,
      challengeeIDs: Vector[UUID],
      formatID: UUID,
      bestOf: Int)
      extends LadderMessage {
    require(bestOf % 2 == 1 && bestOf > 0) // Must be an odd number of matches
  }
  case class RespondToChallenge(challengeID: UUID, accepted: Boolean)
      extends LadderMessage
  case class ReportResults(challengeID: Option[UUID], replay: Replay)
      extends LadderMessage
}

sealed trait LadderMessage extends GGMessage {
  val service = "ladder"
}
