package io.ggtour.ladder.formats

import java.net.URI
import java.util.UUID

import io.ggtour.ladder.elo.GameResult

import scala.collection.mutable

trait LadderFormat {
  val id: UUID
  val name: String
  val discordLink: URI
  val mapPool: Set[UUID]
  val motws: Set[UUID]

  val requiredPlayers: List[ForceSpec]
  def updateRatings(results: List[GameResult]): Map[UUID, Int]

  LadderFormats.register(this)
}

case class ForceSpec(players: Int)

object LadderFormats {
  private var formats: mutable.HashMap[UUID, LadderFormat] = mutable.HashMap()
  private[formats] def register(format: LadderFormat) =
    formats += (format.id -> format)

  def getFormat(formatID: UUID): Option[LadderFormat] = formats.get(formatID)
}
