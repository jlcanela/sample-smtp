package org.ansoft.smtp.sender

import akka.actor.{Props, ActorLogging, Actor}
import javax.mail.internet.MimeMessage
import com.sun.mail.smtp.SMTPTransport
import javax.mail.{Session, Message, URLName}
import org.ansoft.smtp.reaper.Reaper

class SmtpSenderActor(session: Session) extends Actor with ActorLogging {

  val transport = new SMTPTransport(session, new URLName("127.0.0.1:25"))

  transport.connect()

  def receive = {
    case m: MimeMessage ⇒
      transport.sendMessage(m, m.getRecipients(Message.RecipientType.TO))
  }

  override def preStart {
    context.actorSelection("/user/reaper") ! Reaper.WatchMe(context.self)
  }

  override def postStop {
    transport.close
  }
}

object SmtpSenderActor {
  def props(session: Session): Props = Props(new SmtpSenderActor(session))
}