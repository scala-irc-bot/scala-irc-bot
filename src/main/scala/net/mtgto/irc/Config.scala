package net.mtgto.irc

trait Config {
  // IRC Server
  val hostname: String
  val port: Int
  val password: Option[String]
  val encoding: String
  val delay: Int

  val nickname: String
  val username: String
  val realname: String

  // Channels
  val channels: Array[String]
  
  // bot names
  val bots: Array[String]
}
