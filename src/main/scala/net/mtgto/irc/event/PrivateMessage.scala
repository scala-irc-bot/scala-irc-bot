package net.mtgto.irc.event

import java.util.Date

/**
 * a private message someone sent to the irc bot.
 */
case class PrivateMessage(
  nickname: String,
  username: String,
  hostname: String,
  text: String,
  override val date: Date
) extends Event
