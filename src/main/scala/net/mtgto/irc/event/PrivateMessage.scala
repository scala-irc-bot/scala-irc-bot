package net.mtgto.irc.event

/**
 * a private message someone sent to the irc bot.
 */
case class PrivateMessage(
  nickname: String,
  username: String,
  hostname: String,
  text: String
) extends Event
