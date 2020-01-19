package io.ggtour.ladder.elo

import java.util.UUID

import io.ggtour.ladder.formats.LadderFormat

abstract class HandlesGameResults(protected val format: LadderFormat) {

  /**
    * Return elo changes for each account.
    *
    * @param result Map of accounts to results.
    * @return Delta of the elos for each player.
    */
  def getEloChange(result: GameResult): Map[UUID, Int]

}
