package net.mtgto.irc.event

/**
 * a message someone sent to a channel.
 */
case class Message(
  channel: String,
  nickname: String,
  username: String,
  hostname: String,
  text: String
) extends Event
