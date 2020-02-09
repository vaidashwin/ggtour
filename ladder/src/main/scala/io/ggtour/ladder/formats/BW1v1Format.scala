package io.ggtour.ladder.formats
import java.net.URI
import java.util.UUID

import io.ggtour.ladder.elo.GameResult

object BW1v1Format extends LadderFormat {
  override val id: UUID = UUID.fromString("f8f4732e-e0b7-4a93-b10e-8ea1238f4699")
  override val name: String = "1v1"
  override val discordServerIDs: List[Long] = 666095785175416895L :: Nil // todos: maybe this should be a list of IDs or channel IDs?
  override val mapPool: Set[UUID] = Set()
  override val motws: Set[UUID] = Set()
  override val requiredPlayers: List[ForceSpec] = ForceSpec(1) :: ForceSpec(1) :: Nil

  override def updateRatings(results: List[GameResult]): Map[UUID, Int] = ???
}
