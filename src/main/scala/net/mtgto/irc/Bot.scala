package net.mtgto.irc

import event.Message

trait Bot {
  def onMessage(client: Client, message: Message): Unit = {}
}
