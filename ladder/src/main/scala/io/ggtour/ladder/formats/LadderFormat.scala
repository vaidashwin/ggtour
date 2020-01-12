package io.ggtour.ladder.formats

import java.util.UUID

trait LadderFormat {
  val name: String
  val mapPool: Set[UUID]
  val motws: Set[UUID]

  val requiredPlayers: List[ForceSpec]
}

case class ForceSpec(players: Int)