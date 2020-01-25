package io.ggtour.account.model

import java.util.UUID

import io.ggtour.common.storage.GGStorage
import io.ggtour.common.storage.GGStorage.api._
import slick.lifted.Tag

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class AccountTable(tag: Tag) extends Table[Account](tag, "account") {
  def accountID: Rep[UUID] = column[UUID]("account_id", O.PrimaryKey)
  def username: Rep[String] = column[String]("username")
  def eloByFormat: Rep[Map[String, String]] =
    column[Map[String, String]]("elo_by_format")(GGStorage.api.simpleHStoreTypeMapper)
  def battleNetID: Rep[String] = column[String]("battle_net_id")
  def discordID: Rep[String] = column[String]("discord_id")
  // Index on discord ID for quicker lookup from discord service
  def discordIdx = index("idx_account_discord_id", discordID, unique = true)
  // Convert the map string/string to the appropriate types. Backing hstore only supports string/string.
  override def * =
    (accountID, username, eloByFormat, battleNetID, discordID) <> ({
      case (aID, username, eloByFormatMapString, battleNetID, discordID) =>
        val eloByFormatMap = eloByFormatMapString.flatMap {
          case (formatString, eloString) =>
            Try(UUID.fromString(formatString) -> eloString.toInt).toOption
        }
        discordID match {
          case DiscordID(id) =>
            Account(aID, username, eloByFormatMap, battleNetID, id)
          case _ => null
        }
    },
    (account: Account) => account match {
      case Account(aID, username, eloByFormatMap, battleNetID, discordID) =>
        val eloByFormatMapString = eloByFormatMap.map {
          case (format, elo) => format.toString -> elo.toString
        }
        Some((aID, username, eloByFormatMapString, battleNetID, discordID.toString))
    })
}

case class Account(
    accountID: UUID,
    username: String,
    eloByFormat: Map[UUID, Int],
    battleNetID: String,
    discordID: DiscordID,
)

object Account {
  import io.ggtour.common.storage.GGStorage.api._
  private val accountIDQuery = {
    val query = (aID: Rep[UUID]) =>
      TableQuery[AccountTable].filter(_.accountID === aID).take(1)
    Compiled(query(_))
  }
  def forAccountID(accountID: UUID)(
      implicit executionContext: ExecutionContext): Future[Account] =
    GGStorage.db
      .run(accountIDQuery(accountID).result)
      .map(_.headOption.getOrElse(
        throw new RuntimeException("Account doesn't exist")))

  private val discordIDQuery = {
    val query = (dID: Rep[String]) =>
      TableQuery[AccountTable].filter(_.discordID === dID).take(1)
    Compiled(query(_))
  }
  def forDiscordID(discordID: DiscordID)(
      implicit executionContext: ExecutionContext): Future[Account] =
    GGStorage.db
      .run(discordIDQuery(discordID.toString).result)
      .map(_.headOption.getOrElse(
        throw new RuntimeException("Account doesn't exist")))

}
