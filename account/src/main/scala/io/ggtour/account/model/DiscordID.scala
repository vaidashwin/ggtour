package io.ggtour.account.model

import scala.util.Try

case class DiscordID(username: String, tag: Short) {
  override def toString: String = s"$username#$tag"
}

object DiscordID {
  private val discordFormat = "(\\w+)#(\\d+)".r()
  def unapply(discordString: String): Option[DiscordID] = discordString match {
    case discordFormat(username, tag) =>
      Try(DiscordID(username, tag.toShort)).toOption
    case _ => None
  }
}
