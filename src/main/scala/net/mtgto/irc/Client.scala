package net.mtgto.irc

import config.BotConfig
import event.{Message, PrivateMessage}

import org.jibble.pircbot.PircBot
import com.twitter.util.Eval
import org.slf4j.LoggerFactory

import java.io.File
import java.util.Date

object Client {
  protected[this] val setting: Config = new Eval()(new File("config/Config.scala"))

  protected[this] val innerClient: InnerClient = new InnerClient(setting.encoding, setting.nickname, setting.username, setting.realname, setting.delay)

  protected[this] val channelNames: collection.mutable.HashSet[String] = collection.mutable.HashSet.empty[String]

  protected[this] val users: collection.mutable.HashMap[String, User] = collection.mutable.HashMap.empty[String, User]

  protected[this] val bots: Seq[Bot] = setting.bots map {
    bot => loadBot(bot._1, bot._2)
  }

  protected[this] def loadBot(className: String, botConfig: Option[BotConfig]): Bot = {
    import java.net.URLClassLoader

    val directory = new File("bots")

    val loader = new URLClassLoader(
      directory.list map (new File(directory, _).toURI.toURL),
      this.getClass.getClassLoader
    )
    botConfig match {
      case Some(botConfig) =>
        loader.loadClass(className).getConstructor(botConfig.getClass).newInstance(botConfig).asInstanceOf[Bot]
      case None =>
        loader.loadClass(className).newInstance.asInstanceOf[Bot]
    }
  }

  protected[Client] def onMessage(message: Message) = {
    bots foreach (_.onMessage(message))
  }

  protected[Client] def onPrivateMessage(message: PrivateMessage) = {
    bots foreach (_.onPrivateMessage(message))
  }
  
  /**
   * send a notice message to the target (means the channel or username).
   */
  def sendNotice(target: String, text: String) = {
    innerClient.sendNotice(target, text)
  }

  def main(args: Array[String]) {
    innerClient.connect(setting.hostname, setting.port)
    for (channel <- setting.channels) {
      innerClient.joinChannel(channel)
    }

    while (true) {
      val line = readLine("> ")
      if (line == "exit") {
        sys.exit
      }
    }
  }

  class InnerClient(
    val encoding: String,
    val nickname: String,
    val username: String,
    val realname: String,
    val delay: Long
  ) extends PircBot {
    val logger = LoggerFactory.getLogger(this.getClass)
    
    setEncoding(encoding)
    setName(nickname)
    setLogin(username)
    setVersion(realname)
    setMessageDelay(delay)

    override protected def onMessage(channel: String, sender: String, login: String, hostname: String, message: String) = {
      Client.onMessage(Message(channel, sender, login, hostname, message, new Date))
    }

    override protected def onPrivateMessage(sender: String, login: String, hostname: String, message: String) = {
      Client.onPrivateMessage(PrivateMessage(sender, login, hostname, message, new Date))
    }

    override protected def onNotice(sourceNick: String, sourceLogin: String, sourceHostname: String, target: String, notice: String) = {
      //bots foreach (_.onNotice(this, sourceNick, sourceLogin, sourceHostname, target, notice))
    }

    override protected def onInvite(targetNick: String, sourceNick: String, sourceLogin: String, sourceHostname: String, channel: String) = {
      //bots foreach (_.onInvite(this, targetNick, sourceNick, sourceLogin, sourceHostname, channel))
    }

    override protected def onJoin(channel: String, sender: String, login: String, hostname: String) = {
      //bots foreach (_.onJoin(this, channel, sender, login, hostname))
    }

    override protected def onKick(channel: String, kickerNick: String, kickerLogin: String, kickerHostname: String, recipientNick: String, reason: String) = {
      //bots foreach (_.onKick(this, channel, kickerNick, kickerLogin, kickerHostname, recipientNick, reason))
    }

    override protected def onMode(channel: String, sourceNick: String, sourceLogin: String, sourceHostname: String, mode: String) = {
      //bots foreach (_.onMode(this, channel, sourceNick, sourceLogin, sourceHostname, mode))
    }

    override protected def onTopic(channel: String, topic: String, setBy: String, date: Long, changed: Boolean) = {
      //bots foreach (_.onTopic(this, channel, topic, setBy, date, changed))
    }

    override protected def onNickChange(oldNick: String, login: String, hostname: String, newNick: String) = {
      //bots foreach (_.onNickChange(this, oldNick, login, hostname, newNick))
    }

    override protected def onOp(channel: String, sourceNick: String, sourceLogin: String, sourceHostname: String, recipient: String) = {
      //bots foreach (_.onOp(this, channel, sourceNick, sourceLogin, sourceHostname, recipient))
    }

    override protected def onPart(channel: String, sender: String, login: String, hostname: String) = {
      //bots foreach (_.onPart(this, channel, sender, login, hostname))
    }

    override protected def onQuit(sourceNick: String, sourceLogin: String, sourceHostname: String, reason: String) = {
      //bots foreach (_.onQuit(this, sourceNick, sourceLogin, sourceHostname, reason))
    }
  }

}
