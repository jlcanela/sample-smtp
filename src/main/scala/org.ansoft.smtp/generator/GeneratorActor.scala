package org.ansoft.smtp.generator

import akka.actor.{PoisonPill, Props, ActorLogging, Actor}
import javax.mail.internet.{MimeBodyPart, MimeMultipart, InternetAddress, MimeMessage}
import javax.mail.{Session, Message}
import org.ansoft.smtp.reaper.Reaper

class GeneratorActor(session: Session) extends Actor with ActorLogging {

  case class EMail(from: String, to: String) {
    val message = "a" * 1024 * 100
  }

  def createMsg(email: EMail) = {
    // create a message
    val mesg = new MimeMessage(session)

    // From Address - this should come from a Properties...
    mesg.setFrom(new InternetAddress(email.from))

    // TO Address
    val toAddress = new InternetAddress(email.to)
    mesg.addRecipient(Message.RecipientType.TO, toAddress)

    // The Subject
    mesg.setSubject("message_subject"+System.currentTimeMillis)

    // Now the message body.
    val mp = new MimeMultipart()

    val textPart = new MimeBodyPart()
    textPart.setText("message_body") // sets type to "text/plain"

    val pixPart = new MimeBodyPart()
    pixPart.setContent("a" * (1024*100), "text/html")

    // Collect the Parts into the MultiPart
    mp.addBodyPart(textPart)
    mp.addBodyPart(pixPart)

    // Put the MultiPart into the Message
    mesg.setContent(mp)

    mesg
  }

  def receive = {
    case i:Int ⇒ context.actorSelection("/user/smtp") ! createMsg(EMail(from = "jlcanela@videoteam.com", to = "jlcanela@videoteam.com"))
    case GeneratorActor.Stop ⇒
      context.parent ! PoisonPill
      context.actorSelection("/user/smtp/*") ! PoisonPill
  }

  override def preStart {
    context.actorSelection("/user/reaper") ! Reaper.WatchMe(context.self)
  }

}

object GeneratorActor {

  object Stop

  def props(session: Session): Props = Props(new GeneratorActor(session))
}