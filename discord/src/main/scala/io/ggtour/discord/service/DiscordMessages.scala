package io.ggtour.discord.service

import ackcord.data.User
import io.ggtour.account.model.DiscordID
import io.ggtour.common.service.{GGMessage, GGResponse}

object DiscordMessages {
  case class DMUser(discordID: DiscordID, message: String) extends DiscordMessage
  case class MentionUser(discordID: DiscordID, channelID: Int, message: String) extends DiscordMessage

  // Bot messages
  case class LfgFromDiscord(sender: User) extends DiscordMessage
}

sealed trait DiscordMessage extends GGMessage

case class DiscordResponse[T](override val payload: T) extends GGResponse(payload) with DiscordMessage