package net.mtgto.irc

case class User(
  val nickname: String,
  val hasVoice: Boolean,
  val isOperator: Boolean
)
