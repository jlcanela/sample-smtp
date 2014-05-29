package org.ansoft.smtp.reader

import akka.actor.{PoisonPill, ActorLogging, Actor}
import org.ansoft.smtp.reader.ReaderActor.Read

class ReaderActor extends Actor with ActorLogging {

  val iter = (1 to 5000).toIterator

  def receive = {
    case Read ⇒ {
      if (iter.hasNext) {
        val n = iter.next
        context.actorSelection("/user/generator").forward(n)
      } else {
        context.sender ! PoisonPill
      }

    }
  }
}

object ReaderActor {
  object Read
}