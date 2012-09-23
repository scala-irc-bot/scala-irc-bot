package net.mtgto.irc

import config.BotConfig
import event._

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

  protected[Client] def onNotice(notice: Notice) = {
    bots foreach (_.onNotice(notice))
  }

  protected[Client] def onInvite(invite: Invite) = {
    bots foreach (_.onInvite(invite))
  }

  protected[Client] def onJoin(join: Join) = {
    bots foreach (_.onJoin(join))
  }

  protected[Client] def onKick(kick: Kick) = {
    bots foreach (_.onKick(kick))
  }

  protected[Client] def onMode(mode: Mode) = {
    bots foreach (_.onMode(mode))
  }

  protected[Client] def onTopic(topic: Topic) = {
    bots foreach (_.onTopic(topic))
  }

  protected[Client] def onNickChange(nickChange: NickChange) = {
    bots foreach (_.onNickChange(nickChange))
  }

  protected[Client] def onOp(op: Op) = {
    bots foreach (_.onOp(op))
  }

  protected[Client] def onPart(part: Part) = {
    bots foreach (_.onPart(part))
  }

  protected[Client] def onQuit(quit: Quit) = {
    bots foreach (_.onQuit(quit))
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
      Client.onNotice(Notice(target, sourceNick, sourceLogin, sourceHostname, notice, new Date))
    }

    override protected def onInvite(targetNick: String, sourceNick: String, sourceLogin: String, sourceHostname: String, channel: String) = {
      Client.onInvite(Invite(channel, targetNick, sourceNick, sourceLogin, sourceHostname, new Date))
    }

    override protected def onJoin(channel: String, sender: String, login: String, hostname: String) = {
      Client.onJoin(Join(channel, sender, login, hostname, new Date))
    }

    override protected def onKick(channel: String, kickerNick: String, kickerLogin: String, kickerHostname: String, recipientNick: String, reason: String) = {
      Client.onKick(Kick(channel, recipientNick, kickerNick, kickerLogin, kickerHostname, reason, new Date))
    }

    override protected def onMode(channel: String, sourceNick: String, sourceLogin: String, sourceHostname: String, mode: String) = {
      Client.onMode(Mode(channel, sourceNick, sourceLogin, sourceHostname, mode, new Date))
    }

    override protected def onTopic(channel: String, topic: String, setBy: String, date: Long, changed: Boolean) = {
      // TODO 'changed' is ignored.
      Client.onTopic(Topic(channel, setBy, topic, new Date(date)))
    }

    override protected def onNickChange(oldNick: String, login: String, hostname: String, newNick: String) = {
      Client.onNickChange(NickChange(oldNick, newNick, login, hostname, new Date))
    }

    override protected def onOp(channel: String, sourceNick: String, sourceLogin: String, sourceHostname: String, recipient: String) = {
      Client.onOp(Op(channel, recipient, sourceNick, sourceLogin, sourceHostname, new Date))
    }

    override protected def onPart(channel: String, sender: String, login: String, hostname: String) = {
      Client.onPart(Part(channel, sender, login, hostname, new Date))
    }

    override protected def onQuit(sourceNick: String, sourceLogin: String, sourceHostname: String, reason: String) = {
      Client.onQuit(Quit(sourceNick, sourceLogin, sourceHostname, reason, new Date))
    }
  }
}
