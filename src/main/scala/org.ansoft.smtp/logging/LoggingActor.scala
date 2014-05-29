package org.ansoft.smtp.logging

import akka.actor.{ActorLogging, Actor}

class LoggingActor extends Actor with ActorLogging {

  def receive = {
    case ("ERROR", msg:String) ⇒ log.error(msg)
    case ("INFO", msg:String) ⇒ log.info(msg)
  }

}
