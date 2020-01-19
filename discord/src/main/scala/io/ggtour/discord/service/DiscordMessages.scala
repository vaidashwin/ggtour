package io.ggtour.discord.service

import io.ggtour.account.model.DiscordID

object DiscordMessages {
  case class DMUser(discordID: DiscordID, message: String)
  case class MentionUser(discordID: DiscordID, channelID: Int, message: String)
}
