package net.mtgto.irc

import akka.actor.Actor

class TimerActor extends Actor {
  def receive = {
    case (client: Client, currentTimeMillis) => {
      client.bots foreach (_.onTimer(client))
    }
  }
}
