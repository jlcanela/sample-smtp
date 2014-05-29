package org.ansoft.smtp.generator

import akka.actor.{Props, ActorLogging, Actor}
import javax.mail.internet.{MimeBodyPart, MimeMultipart, InternetAddress, MimeMessage}
import javax.mail.{Session, Message}

import org.ansoft.smtp.reader.ReaderActor

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
    case i:Int ⇒ {
      try {
        //context.sender ! createMsg(EMail(from = "fromé|\t@example.com", to = "sampleé\t@example.com"))
        context.sender ! createMsg(EMail(from = "from@example.com", to = "sample@example.com"))
      } catch {
        case ex: Exception =>
          context.actorSelection("/user/logging") ! ("ERROR", ex.getMessage)
          context.actorSelection("/user/reader").forward(ReaderActor.Read)
      }

    }
  }

}

object GeneratorActor {

  object Stop

  def props(session: Session): Props = Props(new GeneratorActor(session))
}