package io.ggtour.ladder.service

import java.util.UUID

import io.ggtour.core.service.GGMessage
import io.ggtour.game.replay.Replay

object LadderMessages {
  case class ChallengePlayer(challengerID: UUID, playerID: UUID, formatID: UUID, bestOf: Int) extends GGMessage {
    require(bestOf % 2 == 1 && bestOf > 0) // Must be an odd number of matches
  }
  case class RespondToChallenge(challengeID: UUID, accepted: Boolean) extends GGMessage
  case class ReportResults(challengeID: Option[UUID], replay: Replay) extends GGMessage
}