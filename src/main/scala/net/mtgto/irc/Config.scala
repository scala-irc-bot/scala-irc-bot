package net.mtgto.irc

import config.BotConfig

/**
 * IRC Client configuration
 *
 * @param hostname IRC server hostname (ex. "irc.freenode.net")
 * @param port IRC server port (ex. 6667)
 * @param password IRC server password (ex. Some("secret"))
 * @param encoding default encoding (ex. "UTF-8")
 * @param messageDelay delay interval (msec) between sending messages
 * @param timerDelay delay interval (msec) between calling onTimer events
 * @param nickname nickname
 * @param username username
 * @param realname realname
 * @param channels array of channel name
 * @param bots array of FQCN and configuration of bots
 */
trait Config {
  // IRC Server
  val hostname: String
  val port: Int
  val password: Option[String]
  val encoding: String
  val messageDelay: Int
  val timerDelay: Int

  val nickname: String
  val username: String
  val realname: String

  // Channels
  val channels: Array[String]

  // bot names
  val bots: Array[(String, Option[BotConfig])]
}
