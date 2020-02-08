package io.ggtour.discord.service

import io.ggtour.common.service.{GGResponse, ServiceName}

object DiscordServiceName extends ServiceName[DiscordMessage] {
  override val name: String = "discord"
  override def replyWith[U](payload: U): GGResponse[U] with DiscordMessage = DiscordResponse(payload)
}
