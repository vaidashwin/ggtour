package io.ggtour.discord.service

import io.ggtour.common.service.ServiceName

object DiscordServiceName extends ServiceName[DiscordMessage] {
  override val name: String = "discord"
}
