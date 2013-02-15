package net.mtgto.irc

import org.slf4j.Logger

import java.io.File

/**
 * Interface of the client which is connecting to the IRC server.
 */
trait Client {
  /**
   * The logger of this client
   */
  val logger: Logger

  /**
   * Connect to the IRC server.
   */
  def connect: Unit

  /**
   * Disconnect from the IRC server.
   */
  def disconnect: Unit

  /**
   * Whether or not bot is connected.
   */
  def isConnected: Boolean

  /**
   * find a bot by its FQCN.
   *
   * @param name FQCN of the specified bot.
   * @return bot The instance if exists.
   */
  def getBot(name: String): Option[Bot]

  /**
   * get user's nicknames in specified channel.
   *
   * @param channel The name of the channel
   * @return The set of nicknames in the specified channel.
   */
  def getUsers(channel: String): Set[String]

  /**
   * send a notice message to the target (means the channel or username).
   *
   * @param target name of channel or username to send a notice.
   * @param text The text of a notice message.
   */
  def sendNotice(target: String, text: String)

  /**
   * send a message to the target (means the channel or username).
   *
   * @param target name of channel or username to send a message.
   * @param text The text of a message.
   */
  def sendMessage(target: String, text: String)

  /**
   * send a raw line
   *
   * @param line raw message
   */
  def sendRawLine(line: String)

  /**
   * send a file on DCC.
   *
   * @param nick The target
   * @param file The file to send
   * @param timeout Timeout milliseconds
   */
  def sendDccFile(nick: String, file: File, timeout: Int = 120000): Unit
}
