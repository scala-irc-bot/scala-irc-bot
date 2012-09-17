package net.mtgto.irc.event

import java.util.Date

sealed trait Event {
  val date = new Date
}

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
