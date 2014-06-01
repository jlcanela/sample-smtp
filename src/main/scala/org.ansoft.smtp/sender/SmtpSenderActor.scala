package org.ansoft.smtp.sender

import akka.actor.{Props, ActorLogging, Actor}
import javax.mail.internet.MimeMessage
import com.sun.mail.smtp.SMTPTransport
import javax.mail.{Session, Message, URLName}
import org.ansoft.smtp.reaper.Reaper
import org.ansoft.smtp.reader.ReaderActor
import scala.concurrent.duration._

class SmtpSenderActor(session: Session) extends Actor with ActorLogging {

  var transport : Option[SMTPTransport] = None

  def reconnectIfRequired {
    if (transport.map(_.isConnected) getOrElse false) {
      // we're connected so assuming it's ok
    } else {
      try {
        val t = new SMTPTransport(session, new URLName("127.0.0.1:25"))
        t.connect()
        transport = Some(t)
      } catch {
        case ex: Exception =>
          context.actorSelection("/user/logging") ! ("ERROR", ex.getMessage)
      }

    }
  }

  def resend(m:MimeMessage) {
    import scala.concurrent.ExecutionContext.Implicits.global
    context.system.scheduler.scheduleOnce(5 seconds) {
      self ! m
    }
  }

  def sendMessage(m:MimeMessage) = try {
    transport.map(t => {
      t.sendMessage(m, m.getRecipients(Message.RecipientType.TO))
      context.actorSelection("/user/logging") ! ("INFO", "sent email")
      context.actorSelection("/user/reader") ! ReaderActor.Read
    }) getOrElse {
      resend(m)
    }
  } catch {
    case ex: Exception =>
      transport.map(_.close)
      context.actorSelection("/user/logging") ! ("ERROR", ex.getMessage)
      resend(m)
  }

  def receive = {
    case m: MimeMessage ⇒
      reconnectIfRequired
      sendMessage(m)
  }

  override def preStart {
    context.actorSelection("/user/reaper") ! Reaper.WatchMe(context.self)
    context.actorSelection("/user/reader") ! ReaderActor.Read
  }

  override def postStop {
    transport.map(_.close)
  }

}

object SmtpSenderActor {
  def props(session: Session): Props = Props(new SmtpSenderActor(session))
}