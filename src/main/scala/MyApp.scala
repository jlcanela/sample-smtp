
import com.sun.mail.smtp.SMTPTransport
import java.io.IOException;
import java.util.Properties;

import javax.mail._
;





import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

case class EMail(from: String, to: String) {
  def message = "a" * 1024 * 100
}

object MyApp {

  val props = new Properties();
  props.setProperty("mail.transport.protocol", "smtp");
  props.setProperty("mail.host", "127.0.0.1");

  val session = Session.getInstance(props);

  def sendMail(email: EMail) {
    val props = new Properties

    val transport = new SMTPTransport(session, new URLName("127.0.0.1:25"))

    // create a message
    val mesg = new MimeMessage(session);

    // From Address - this should come from a Properties...
    mesg.setFrom(new InternetAddress(email.from));

    // TO Address
    val toAddress = new InternetAddress(email.to);
    mesg.addRecipient(Message.RecipientType.TO, toAddress);

    // The Subject
    mesg.setSubject("message_subject");

    // Now the message body.
    val mp = new MimeMultipart();

    val textPart = new MimeBodyPart();
    textPart.setText("message_body"); // sets type to "text/plain"

    val pixPart = new MimeBodyPart();
    pixPart.setContent("a" * (1024*100), "text/html");

    // Collect the Parts into the MultiPart
    mp.addBodyPart(textPart);
    mp.addBodyPart(pixPart);

    // Put the MultiPart into the Message
    mesg.setContent(mp);

    // Finally, send the message!
    transport.connect();
    transport.sendMessage(mesg, mesg.getRecipients(Message.RecipientType.TO))
    transport.close
  }

  def main(args: Array[String]) {

    val e = EMail(from = "jlcanela@videoteam.com", to = "jlcanela@videoteam.com")

    val start = System.currentTimeMillis()
    val count = 10000


    val seq = (1 to count).map { _ =>
      future {
        sendMail(e)
      }
    }

    Await.result(Future.sequence(seq), 100 seconds)
    //Future.await

    val end = System.currentTimeMillis()

    println(s"$count - ${end-start}")

  }

}
