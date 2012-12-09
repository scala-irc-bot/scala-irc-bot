package net.mtgto.irc

import event._

trait Bot {
  def onMessage(client: Client, message: Message) = {}

  def onPrivateMessage(client: Client, message: PrivateMessage) = {}

  def onNotice(client: Client, notice: Notice) = {}

  def onInvite(client: Client, invite: Invite) = {}

  def onJoin(client: Client, join: Join) = {}

  def onKick(client: Client, kick: Kick) = {}

  def onMode(client: Client, mode: Mode) = {}

  def onTopic(client: Client, topic: Topic) = {}

  def onNickChange(client: Client, nickChange: NickChange) = {}

  def onOp(client: Client, op: Op) = {}

  def onPart(client: Client, part: Part) = {}

  def onQuit(client: Client, quit: Quit) = {}
}
