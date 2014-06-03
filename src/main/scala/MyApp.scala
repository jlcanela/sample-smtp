
import com.sun.mail.smtp.SMTPTransport
import java.io.{InputStreamReader, BufferedReader, PrintWriter}
import java.net.Socket
import java.util.Properties

import javax.mail._


import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

case class EMail(from: String, to: String) {
  val message = "a" * 1024 * 100
}



object MyApp extends SmtpUtil {

  val count = 1000
  val msgpercnx = 10


  def seq = (1 to count).map { _ =>
    future {
      sendMail(email, msgpercnx)
    }
  }

  def send {
    val start = System.currentTimeMillis()
    Await.result(Future.sequence(seq), 100 seconds)
    val end = System.currentTimeMillis()
    println(f"${count*msgpercnx} msgs - ${end-start} ms => ${1.0*count*msgpercnx/(end-start)*1000.}%02.2f msg/s")
  }

  def main(args: Array[String]) {

    send

  }

}
