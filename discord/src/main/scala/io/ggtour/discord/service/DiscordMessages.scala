package io.ggtour.discord.service

import io.ggtour.account.model.DiscordID
import io.ggtour.core.service.GGMessage

object DiscordMessages {
  case class DMUser(discordID: DiscordID, message: String) extends GGMessage
  case class MentionUser(discordID: DiscordID, channelID: Int, message: String) extends GGMessage
}
