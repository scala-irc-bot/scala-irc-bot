package net.mtgto.irc.event

import java.util.Date

sealed trait Event {
  val date = new Date
}

/**
 * a message someone sends to a channel.
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
 * a private message someone sends to the irc bot.
 */
case class PrivateMessage(
  nickname: String,
  username: String,
  hostname: String,
  text: String,
  override val date: Date
) extends Event

/**
 * a notice someone sends to a channel or user.
 */
case class Notice(
  target: String,
  sourceNickname: String,
  sourceUsername: String,
  sourceHostname: String,
  text: String,
  override val date: Date
) extends Event

/**
 * someone (source) invites the user (target) to a channel.
 */
case class Invite(
  channel: String,
  targetNickname: String,
  sourceNickname: String,
  sourceUsername: String,
  sourceHostname: String,
  override val date: Date
) extends Event

/**
 * someone joins to a channel.
 */
case class Join(
  channel: String,
  nickname: String,
  username: String,
  hostname: String,
  override val date: Date
) extends Event

/**
 * someone (source) kicks the user (target) on a channel.
 */
case class Kick(
  channel: String,
  targetNickname: String,
  sourceNickname: String,
  sourceUsername: String,
  sourceHostname: String,
  reason: String,
  override val date: Date
) extends Event

/**
 * someone changes the mode of a channel.
 */
case class Mode(
  channel: String,
  nickname: String,
  username: String,
  hostname: String,
  mode: String,
  override val date: Date
) extends Event

/**
 * a channel topic set by someone.
 */
case class Topic(
  channel: String,
  nickname: String,
  topic: String,
  override val date: Date
) extends Event

/**
 * someone changes his nickname.
 */
case class NickChange(
  oldNickname: String,
  newNickname: String,
  username: String,
  hostname: String,
  override val date: Date
) extends Event

/**
 * someone (sourec) adds operator status to the user (target).
 */
case class Op(
  channel: String,
  targetNickname: String,
  sourceNickname: String,
  sourceUsername: String,
  sourceHostname: String,
  override val date: Date
) extends Event

/**
 * someone leaves a channel.
 */
case class Part(
  channel: String,
  nickname: String,
  username: String,
  hostname: String,
  override val date: Date
) extends Event

/**
 * someone quit the connection to the server.
 */
case class Quit(
  nickname: String,
  username: String,
  hostname: String,
  reason: String,
  override val date: Date
) extends Event
