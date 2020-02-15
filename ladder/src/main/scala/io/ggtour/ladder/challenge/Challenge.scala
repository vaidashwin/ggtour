package io.ggtour.ladder.challenge

import java.util.UUID

import io.ggtour.ladder.elo.GameResult

case class Challenge(
    challengerID: UUID, // primary key for Redis
    challengeeIDs: Vector[UUID],
    formatID: UUID,
    bestOf: Int,
    isAccepted: Boolean,
    results: Vector[GameResult]) {
  require(bestOf % 2 == 1 && bestOf > 0) // Must be an odd number of matches
  def isComplete: Boolean =
    true // TODO: check results to see if challenge is completed
}
