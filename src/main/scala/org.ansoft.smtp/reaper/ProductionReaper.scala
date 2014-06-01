package org.ansoft.smtp.reaper

import akka.actor.ActorLogging

class ProductionReaper extends Reaper with ActorLogging {

  val startTime = System.currentTimeMillis()


  // Shutdown
  def allSoulsReaped(): Unit = {
    val endTime = System.currentTimeMillis()
    val totalTime = 1.0 * (endTime - startTime) / 1000
    log.info(f"TotalTime: ${totalTime}%02f")

    context.system.shutdown()
  }

}