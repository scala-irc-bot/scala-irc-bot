package net.mtgto.irc.event

import java.util.Date

/**
 * a message someone sent to a channel.
 */
case class Message(
  channel: String,
  nickname: String,
  username: String,
  hostname: String,
  text: String,
  override val date: Date
) extends Event
