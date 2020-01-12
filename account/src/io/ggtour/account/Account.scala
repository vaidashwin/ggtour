package io.ggtour.account

import java.util.UUID

import io.ggtour.account.discord.DiscordID

case class Account(accountID: UUID,
                   username: String,
                   // b.net oauth info?
                   discordID: DiscordID
                  )
