package net.mtgto.irc

import event._

trait Bot {
  def onMessage(message: Message) = {}

  def onPrivateMessage(message: PrivateMessage) = {}

  def onNotice(notice: Notice) = {}

  def onInvite(invite: Invite) = {}

  def onJoin(join: Join) = {}

  def onKick(kick: Kick) = {}

  def onMode(mode: Mode) = {}

  def onTopic(topic: Topic) = {}

  def onNickChange(nickChange: NickChange) = {}

  def onOp(op: Op) = {}

  def onPart(part: Part) = {}

  def onQuit(quit: Quit) = {}
}
