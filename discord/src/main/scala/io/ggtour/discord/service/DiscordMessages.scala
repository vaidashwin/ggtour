package io.ggtour.discord.service

import java.util.UUID

import ackcord.data.User
import io.ggtour.account.model.DiscordID
import io.ggtour.common.service.GGMessage

object DiscordMessages {
  case class DMUser(discordID: DiscordID, message: String) extends DiscordMessage
  case class MentionUser(discordID: DiscordID, channelID: Int, message: String) extends DiscordMessage

  // Bot messages
  case class LfgFromDiscord(sender: User) extends DiscordMessage
}

sealed trait DiscordMessage extends GGMessage {
  val service = "discord"
}