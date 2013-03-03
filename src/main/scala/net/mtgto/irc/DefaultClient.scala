package net.mtgto.irc

import config.BotConfig
import event._

import akka.actor.{Actor, ActorSystem, Props}

import org.pircbotx.{PircBotX, User => PircUser}
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events._
import com.twitter.util.Eval
import org.slf4j.LoggerFactory

import java.io.File
import java.util.Date

object DefaultClient {
  protected[this] val setting: Config = new Eval()(new File("config/Config.scala"))

  protected[this] val client: Client = new DefaultClient(setting)

  protected[this] val channelNames: collection.mutable.HashSet[String] = collection.mutable.HashSet.empty[String]

  def main(args: Array[String]): Unit = {
    client.connect

    while (readLine("> ") != "exit") {}
    client.disconnect
  }
}

class DefaultClient[T <: PircBotX](val setting: Config) extends ListenerAdapter[T] with Client {
  val logger = LoggerFactory.getLogger(this.getClass)

  protected[this] val innerClient: PircBotX = new PircBotX

  override val bots: Seq[Bot] = setting.bots map {
    bot => loadBot(bot._1, bot._2)
  }

  innerClient.getListenerManager.addListener(this)

  protected[this] val actorSystem = ActorSystem("TimerSystem")
  protected[this] val timerActor = actorSystem.actorOf(Props[TimerActor], "net.mtgto.irc.DefaultClient.TimerActor")

  import actorSystem.dispatcher
  import concurrent.duration.DurationInt
  import language.postfixOps
  actorSystem.scheduler.schedule(60 seconds, setting.timerDelay milliseconds) {
    timerActor ! (this, System.currentTimeMillis)
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

  override def connect = {
    innerClient.setEncoding(setting.encoding)
    innerClient.setName(setting.nickname)
    innerClient.setLogin(setting.username)
    innerClient.setVersion(setting.realname)
    innerClient.setMessageDelay(setting.messageDelay)
    setting.password match {
      case Some(password) =>
        innerClient.connect(setting.hostname, setting.port, password)
      case None =>
        innerClient.connect(setting.hostname, setting.port)
    }
    for (channel <- setting.channels) {
      innerClient.joinChannel(channel)
    }
  }

  override def disconnect = {
    innerClient.quitServer
  }

  override def isConnected: Boolean = {
    innerClient.isConnected
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

  override def sendRawLine(line: String) = {
    innerClient.sendRawLine(line)
  }

  override def sendDccFile(nick: String, file: java.io.File, timeout: Int = 120000) = {
    innerClient.dccSendFile(file, innerClient.getUser(nick), timeout)
  }

  override def onMessage(event: MessageEvent[T]): Unit = {
    val message = Message(
      channel = event.getChannel.getName,
      nickname = event.getUser.getNick,
      username = event.getUser.getLogin,
      hostname = event.getUser.getServer,
      text = event.getMessage,
      date = new Date(event.getTimestamp))
    bots foreach (_.onMessage(this, message))
  }

  override def onPrivateMessage(event: PrivateMessageEvent[T]) = {
    val privateMessage = PrivateMessage(
      nickname = event.getUser.getNick,
      username = event.getUser.getLogin,
      hostname = event.getUser.getServer,
      text = event.getMessage,
      date = new Date(event.getTimestamp))
    bots foreach (_.onPrivateMessage(this, privateMessage))
  }

  override def onNotice(event: NoticeEvent[T]) = {
    val notice = Notice(
      target = event.getChannel.getName,
      sourceNickname = event.getUser.getNick,
      sourceUsername = event.getUser.getLogin,
      sourceHostname = event.getUser.getServer,
      text = event.getMessage,
      date = new Date(event.getTimestamp))
    bots foreach (_.onNotice(this, notice))
  }

  override def onInvite(event: InviteEvent[T]) = {
    val sender = event.getBot.getUser(event.getUser)
    val invite = Invite(
      channel = event.getChannel,
      targetNickname = event.getBot.getUserBot.getNick,
      sourceNickname = sender.getNick,
      sourceUsername = sender.getLogin,
      sourceHostname = sender.getServer,
      date = new Date(event.getTimestamp))
    bots foreach (_.onInvite(this, invite))
  }

  override def onJoin(event: JoinEvent[T]) = {
    val join = Join(
      channel = event.getChannel.getName,
      nickname = event.getUser.getNick,
      username = event.getUser.getLogin,
      hostname = event.getUser.getServer,
      date = new Date(event.getTimestamp))
    bots foreach (_.onJoin(this, join))
  }

  override def onKick(event: KickEvent[T]) = {
    val kick = Kick(
      channel = event.getChannel.getName,
      targetNickname = event.getRecipient.getNick,
      sourceNickname = event.getSource.getNick,
      sourceUsername = event.getSource.getLogin,
      sourceHostname = event.getSource.getServer,
      reason = event.getReason,
      date = new Date(event.getTimestamp))
    bots foreach (_.onKick(this, kick))
  }

  override def onMode(event: ModeEvent[T]) = {
    val mode = Mode(
      channel = event.getChannel.getName,
      nickname = event.getUser.getNick,
      username = event.getUser.getLogin,
      hostname = event.getUser.getServer,
      mode = event.getMode,
      date = new Date(event.getTimestamp))
    bots foreach (_.onMode(this, mode))
  }

  override def onTopic(event: TopicEvent[T]) = {
    val topic = Topic(
      channel = event.getChannel.getName,
      nickname = event.getUser.getNick,
      topic = event.getTopic,
      date = new Date(event.getTimestamp))
    bots foreach (_.onTopic(this, topic))
  }

  override def onNickChange(event: NickChangeEvent[T]) = {
    val nickChange = NickChange(
      oldNickname = event.getOldNick,
      newNickname = event.getNewNick,
      username = event.getUser.getLogin,
      hostname = event.getUser.getServer,
      date = new Date(event.getTimestamp))
    bots foreach (_.onNickChange(this, nickChange))
  }

  override def onOp(event: OpEvent[T]) = {
    val op = Op(
      channel = event.getChannel.getName,
      targetNickname = event.getRecipient.getNick,
      sourceNickname = event.getSource.getNick,
      sourceUsername = event.getSource.getLogin,
      sourceHostname = event.getSource.getServer,
      date = new Date(event.getTimestamp))
    bots foreach (_.onOp(this, op))
  }

  override def onPart(event: PartEvent[T]) = {
    val part = Part(
      channel = event.getChannel.getName,
      nickname = event.getUser.getNick,
      username = event.getUser.getLogin,
      hostname = event.getUser.getServer,
      date = new Date(event.getTimestamp))
    bots foreach (_.onPart(this, part))
  }

  override def onQuit(event: QuitEvent[T]) = {
    val quit = Quit(
      nickname = event.getUser.getNick,
      username = event.getUser.getLogin,
      hostname = event.getUser.getServer,
      reason = event.getReason,
      date = new Date(event.getTimestamp))
    bots foreach (_.onQuit(this, quit))
  }
}

