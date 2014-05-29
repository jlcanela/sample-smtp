package org.ansoft.smtp.sender

import akka.actor.{Props, ActorLogging, Actor}
import javax.mail.internet.MimeMessage
import com.sun.mail.smtp.SMTPTransport
import javax.mail.{Session, Message, URLName}
import org.ansoft.smtp.reaper.Reaper
import org.ansoft.smtp.reader.ReaderActor

class SmtpSenderActor(session: Session) extends Actor with ActorLogging {

  val transport = new SMTPTransport(session, new URLName("127.0.0.1:25"))

  transport.connect()

  def receive = {
    case m: MimeMessage ⇒
      try {
        transport.sendMessage(m, m.getRecipients(Message.RecipientType.TO))
        context.actorSelection("/user/logging") ! ("INFO", "sent email")
      } catch {
        case ex: Exception => context.actorSelection("/user/logging") ! ("ERROR", ex.getMessage)
      }

      context.actorSelection("/user/reader") ! ReaderActor.Read
  }

  override def preStart {
    context.actorSelection("/user/reaper") ! Reaper.WatchMe(context.self)
    context.actorSelection("/user/reader") ! ReaderActor.Read
  }

  override def postStop {
    transport.close
  }

}

object SmtpSenderActor {
  def props(session: Session): Props = Props(new SmtpSenderActor(session))
}