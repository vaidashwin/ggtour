package io.ggtour.account.service

import java.util.UUID

import io.ggtour.account.model.{Account, DiscordID}
import io.ggtour.common.service.GGResponse.ServiceAndPath
import io.ggtour.common.service.{GGMessage, GGRequest, GGResponse, ServiceName}

object AccountMessages {
  case class GetAccountByDiscordID(
      discordID: DiscordID,
      override val replyTo: ServiceAndPath
  ) extends AccountRequest[Option[Account]]
  case class GetAccount(
      accountID: UUID,
      override val replyTo: ServiceAndPath
  ) extends AccountRequest[Option[Account]]
}

sealed trait AccountMessage extends GGMessage
sealed trait AccountRequest[T] extends GGRequest[T] with AccountMessage

case class AccountResponse[T](value: T)
    extends GGResponse[T](value)
    with AccountMessage
