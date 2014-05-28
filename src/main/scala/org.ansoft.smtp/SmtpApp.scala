package org.ansoft.smtp

import akka.actor.{Props, ActorSystem}
import javax.mail.{Session}
import java.util.Properties
import org.ansoft.smtp.reaper.{ProductionReaper}
import org.ansoft.smtp.sender.SmtpSenderActor
import org.ansoft.smtp.generator.GeneratorActor
import akka.routing.{FromConfig, DefaultResizer, RoundRobinPool}


object SmtpApp {

  val props = new Properties()
  props.setProperty("mail.transport.protocol", "smtp")
  props.setProperty("mail.host", "127.0.0.1")

  val session = Session.getInstance(props)


	def start {
		val system = ActorSystem("MySystem")
    val reaper = system.actorOf(Props[ProductionReaper], name = "reaper")
    val smtp = system.actorOf(FromConfig.props(SmtpSenderActor.props(session)), name = "smtp")
    val generator = system.actorOf(FromConfig.props(GeneratorActor.props(session)), name = "generator")

    (1 to 10000).foreach { i =>
      generator ! i
    }

    generator ! GeneratorActor.Stop
	}

	def main(args: Array[String]) {
    start
	}
}
