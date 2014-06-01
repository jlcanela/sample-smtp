package org.ansoft.smtp

import akka.actor.{Props, ActorSystem}
import javax.mail.{Session}
import java.util.Properties
import org.ansoft.smtp.reaper.{ProductionReaper}
import org.ansoft.smtp.sender.SmtpSenderActor
import org.ansoft.smtp.generator.GeneratorActor
import akka.routing.{FromConfig}
import org.ansoft.smtp.reader.ReaderActor
import org.ansoft.smtp.logging.LoggingActor


object SmtpApp {

  val props = new Properties()
  props.setProperty("mail.transport.protocol", "smtp")
  props.setProperty("mail.host", "127.0.0.1")

  val session = Session.getInstance(props)

	def start {
		val system = ActorSystem("MySystem")
    val reaper = system.actorOf(Props[ProductionReaper], name = "reaper")
    val logging = system.actorOf(Props[LoggingActor], name = "logging")
    val reader = system.actorOf(Props[ReaderActor], name = "reader")
    val smtp = system.actorOf(FromConfig.props(SmtpSenderActor.props(session)), name = "smtp")
    val generator = system.actorOf(FromConfig.props(GeneratorActor.props(session)), name = "generator")
	}

	def main(args: Array[String]) {
    start
	}
}
