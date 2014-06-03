import com.sun.mail.smtp.SMTPTransport
import java.util.Properties
import javax.mail.internet.{MimeBodyPart, MimeMultipart, InternetAddress, MimeMessage}
import javax.mail.{URLName, Message, Session}

trait SmtpUtil {


  val props = new Properties()
  props.setProperty("mail.transport.protocol", "smtp")
  props.setProperty("mail.host", "127.0.0.1")

  val session = Session.getInstance(props)

  val email = EMail(from = "jlcanela@videoteam.com", to = "jlcanela@videoteam.com")

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

  def sendMail(email: EMail, msgpercnx: Int) {
    val props = new Properties

    val transport = new SMTPTransport(session, new URLName("127.0.0.1:25"))
    val m = createMsg(email)

    // Finally, send the message!
    transport.connect()
    (1 to msgpercnx).foreach { _ =>

      transport.sendMessage(m, m.getRecipients(Message.RecipientType.TO))
    }

    transport.close
  }

}