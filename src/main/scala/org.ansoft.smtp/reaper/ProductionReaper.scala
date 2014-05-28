package org.ansoft.smtp.reaper

/**
 * Created by jlcanela on 28/05/14.
 */
class ProductionReaper extends Reaper {
  // Shutdown
  def allSoulsReaped(): Unit = context.system.shutdown()
}