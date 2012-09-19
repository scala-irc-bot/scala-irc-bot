package net.mtgto.irc

import event.{Message, PrivateMessage}

trait Bot {
  def onMessage(message: Message): Unit = {}

  def onPrivateMessage(message: PrivateMessage): Unit = {}
}
