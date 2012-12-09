package net.mtgto.irc

import config.BotConfig
import event._

import org.jibble.pircbot.{PircBot, User => PircUser}
import com.twitter.util.Eval
import org.slf4j.LoggerFactory

import java.io.File
import java.util.Date

object DefaultClient {
  protected[this] val setting: Config = new Eval()(new File("config/Config.scala"))

  protected[this] val client: Client = new DefaultClient(setting)

  protected[this] val channelNames: collection.mutable.HashSet[String] = collection.mutable.HashSet.empty[String]

  def main(args: Array[String]) {
    client.connect

    while (readLine("> ") != "exit") {}
    client.disconnect
  }
}

class DefaultClient(val setting: Config) extends Client {
  val logger = LoggerFactory.getLogger(this.getClass)

  protected[this] val innerClient: InnerClient = new InnerClient(this)

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

  /**
   * a map channel names to user's nicknames.
   */
  protected[this] val channelUsers = collection.mutable.HashMap.empty[String, collection.mutable.Set[String]]

  override protected[irc] def connect = {
    innerClient.connect(setting.hostname, setting.port)
    for (channel <- setting.channels) {
      innerClient.joinChannel(channel)
    }
  }

  override protected[irc] def disconnect = {
    innerClient.disconnect
    innerClient.dispose
  }

  override def getBot(name: String): Option[Bot] = {
    bots.find(_.getClass.getCanonicalName == name)
  }

  override def getUsers(channel: String): Set[String] = {
    channelUsers.get(channel).map(_.toSet).getOrElse(Set.empty[String])
  }

  override def sendNotice(target: String, text: String) = {
    innerClient.sendNotice(target, text)
  }

  override def sendMessage(target: String, text: String) = {
    innerClient.sendMessage(target, text)
  }

  override def sendDccFile(nick: String, file: java.io.File, timeout: Int = 120000) = {
    innerClient.dccSendFile(file, nick, timeout)
  }

  protected[DefaultClient] def onMessage(message: Message) = {
    bots foreach (_.onMessage(this, message))
  }

  protected[DefaultClient] def onPrivateMessage(message: PrivateMessage) = {
    bots foreach (_.onPrivateMessage(this, message))
  }

  protected[DefaultClient] def onNotice(notice: Notice) = {
    bots foreach (_.onNotice(this, notice))
  }

  protected[DefaultClient] def onInvite(invite: Invite) = {
    bots foreach (_.onInvite(this, invite))
  }

  protected[DefaultClient] def onJoin(join: Join) = {
    bots foreach (_.onJoin(this, join))
  }

  protected[DefaultClient] def onKick(kick: Kick) = {
    bots foreach (_.onKick(this, kick))
  }

  protected[DefaultClient] def onMode(mode: Mode) = {
    bots foreach (_.onMode(this, mode))
  }

  protected[DefaultClient] def onTopic(topic: Topic) = {
    bots foreach (_.onTopic(this, topic))
  }

  protected[DefaultClient] def onNickChange(nickChange: NickChange) = {
    bots foreach (_.onNickChange(this, nickChange))
  }

  protected[DefaultClient] def onOp(op: Op) = {
    bots foreach (_.onOp(this, op))
  }

  protected[DefaultClient] def onPart(part: Part) = {
    bots foreach (_.onPart(this, part))
  }

  protected[DefaultClient] def onQuit(quit: Quit) = {
    bots foreach (_.onQuit(this, quit))
  }

  class InnerClient(client: DefaultClient) extends PircBot {
    setEncoding(setting.encoding)
    setName(setting.nickname)
    setLogin(setting.username)
    setVersion(setting.realname)
    setMessageDelay(setting.delay)

    override protected def onMessage(channel: String, sender: String, login: String, hostname: String, message: String) = {
      client.onMessage(Message(channel, sender, login, hostname, message, new Date))
    }

    override protected def onPrivateMessage(sender: String, login: String, hostname: String, message: String) = {
      client.onPrivateMessage(PrivateMessage(sender, login, hostname, message, new Date))
    }

    override protected def onNotice(sourceNick: String, sourceLogin: String, sourceHostname: String, target: String, notice: String) = {
      client.onNotice(Notice(target, sourceNick, sourceLogin, sourceHostname, notice, new Date))
    }

    override protected def onInvite(targetNick: String, sourceNick: String, sourceLogin: String, sourceHostname: String, channel: String) = {
      client.onInvite(Invite(channel, targetNick, sourceNick, sourceLogin, sourceHostname, new Date))
    }

    override protected def onJoin(channel: String, sender: String, login: String, hostname: String) = {
      client.onJoin(Join(channel, sender, login, hostname, new Date))
    }

    override protected def onKick(channel: String, kickerNick: String, kickerLogin: String, kickerHostname: String, recipientNick: String, reason: String) = {
      client.onKick(Kick(channel, recipientNick, kickerNick, kickerLogin, kickerHostname, reason, new Date))
    }

    override protected def onMode(channel: String, sourceNick: String, sourceLogin: String, sourceHostname: String, mode: String) = {
      client.onMode(Mode(channel, sourceNick, sourceLogin, sourceHostname, mode, new Date))
    }

    override protected def onTopic(channel: String, topic: String, setBy: String, date: Long, changed: Boolean) = {
      // TODO 'changed' is ignored.
      client.onTopic(Topic(channel, setBy, topic, new Date))
    }

    override protected def onNickChange(oldNick: String, login: String, hostname: String, newNick: String) = {
      for ((channel, users) <- channelUsers) {
        users -= oldNick
        users += newNick
      }
      client.onNickChange(NickChange(oldNick, newNick, login, hostname, new Date))
    }

    override protected def onOp(channel: String, sourceNick: String, sourceLogin: String, sourceHostname: String, recipient: String) = {
      client.onOp(Op(channel, recipient, sourceNick, sourceLogin, sourceHostname, new Date))
    }

    override protected def onPart(channel: String, sender: String, login: String, hostname: String) = {
      client.onPart(Part(channel, sender, login, hostname, new Date))
    }

    override protected def onQuit(sourceNick: String, sourceLogin: String, sourceHostname: String, reason: String) = {
      client.onQuit(Quit(sourceNick, sourceLogin, sourceHostname, reason, new Date))
    }

    override protected def onUserList(channel: String, users: Array[PircUser]) = {
      channelUsers.getOrElseUpdate(channel, collection.mutable.Set.empty[String]) ++= users.map(_.getNick).toSet
    }
  }
}

