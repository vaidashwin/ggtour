package io.ggtour.account.model

import java.util.UUID

import slick.lifted.Tag
import io.ggtour.common.storage.GGStorage.api._

import scala.concurrent.Future
import scala.util.Try

class AccountTable(tag: Tag)
  extends Table[Account](tag, "account")
{
  def accountID: Rep[UUID] = column[UUID]("account_id", O.PrimaryKey)
  def username: Rep[String] = column[String]("username")
  def eloByFormat: Rep[Map[String, String]] = column[Map[String, String]]("elo_by_format")
  def battleNetID: Rep[String] = column[String]("battle_net_id")
  def discordID: Rep[String] = column[String]("discord_id")
  // Convert the map string/string to the appropriate types. Backing hstore only supports string/string.
  override def * = (accountID, username, eloByFormat, battleNetID, discordID) <> (
    {
      case (aID, username, eloByFormatMapString, battleNetID, discordID) =>
        val eloByFormatMap = eloByFormatMapString.flatMap {
          case (formatString, eloString) => Try(UUID.fromString(formatString) -> eloString.toInt).toOption
        }
        Account(aID, username, eloByFormatMap, battleNetID, discordID)
    },
    Account.unapply(_).map {
        case (aID, username, eloByFormatMap, battleNetID, discordID) =>
          val eloByFormatMapString = eloByFormatMap.map { case (format, elo) => format.toString -> elo.toString }
          (aID, username, eloByFormatMapString, battleNetID, discordID)
    }
  )
}

case class Account(accountID: UUID,
                   username: String,
                   eloByFormat: Map[UUID, Int],
                   battleNetID: String,
                   discordID: String,
                  )