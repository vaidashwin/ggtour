package io.ggtour.ladder.formats

import java.net.URI
import java.util.UUID

import io.ggtour.ladder.elo.GameResult

import scala.collection.mutable

trait LadderFormat {
  val id: UUID
  val name: String
  val discordServerIDs: List[Long] // could be discordChannelIDs? not 100% sure on the preferred UX
  val mapPool: Set[UUID]
  val motws: Set[UUID]

  val requiredPlayers: List[ForceSpec]
  def updateRatings(results: List[GameResult]): Map[UUID, Int]

  LadderFormats.register(this)
}

case class ForceSpec(players: Int)

object LadderFormats {
  private val formats: mutable.HashMap[UUID, LadderFormat] = mutable.HashMap()
  private val formatsByDiscordID: mutable.HashMap[Long, LadderFormat] = mutable.HashMap()
  private[formats] def register(format: LadderFormat): Unit = {
    formats += (format.id -> format)
    format.discordServerIDs.foreach(id => formatsByDiscordID += (id -> format))
  }

  def getFormat(formatID: UUID): Option[LadderFormat] = formats.get(formatID)
  def apply(discordID: Long): Option[LadderFormat] = formatsByDiscordID.get(discordID)
}
