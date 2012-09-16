package net.mtgto.irc

import event.{Message, PrivateMessage}

import org.jibble.pircbot.PircBot
import com.twitter.util.Eval
import org.slf4j.LoggerFactory
import java.io.File

object Client extends App {
  protected[this] val setting: Config = new Eval()(new File("config/Config.scala"))

  protected[this] val client = new Client(setting)

  while (true) {
    val line = readLine("> ")
    if (line == "exit") {
      sys.exit
    }
  }
}

class Client(
  val setting: Config
) {
  protected[this] val innerClient: InnerClient = new InnerClient(setting.encoding, setting.nickname, setting.username, setting.realname, setting.delay)

  protected[this] val channelNames: collection.mutable.HashSet[String] = collection.mutable.HashSet.empty[String]

  protected[this] val users: collection.mutable.HashMap[String, User] = collection.mutable.HashMap.empty[String, User]

  innerClient.connect(setting.hostname, setting.port)
  for (channel <- setting.channels) {
    innerClient.joinChannel(channel)
  }

  protected[this] def onMessage(message: Message) = {
    
  }

  def sendNotice(target: String, text: String) = {
    innerClient.sendNotice(target, text)
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
      Client.this.onMessage(Message(channel, sender, login, hostname, message))
      //bots foreach (_.onMessage(this, channel, sender, login, hostname, message))
    }

    override protected def onPrivateMessage(sender: String, login: String, hostname: String, message: String) = {
      val msg = PrivateMessage(sender, login, hostname, message)
      //bots foreach (_.onPrivateMessage(this, sender, login, hostname, message))
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
