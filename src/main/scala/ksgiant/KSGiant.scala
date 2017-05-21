package ksgiant

import events.{damageEvents, deathEvents, spawnEvents, stoneGiantEvent}
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class KSGiant extends JavaPlugin{
  private val damageEvents = new damageEvents(this)
  private val deathEvents = new deathEvents
  private val spawnEvents = new spawnEvents
  var t: Thread = _
  var threadVar = true
  private val stoneGiantEvent = new stoneGiantEvent(this)

  override def onEnable(): Unit ={
    val pm = getServer.getPluginManager


    pm.registerEvents(damageEvents, this)
    pm.registerEvents(deathEvents, this)
    pm.registerEvents(spawnEvents, this)
    t = new Thread(stoneGiantEvent)
    t.start()
    Bukkit.getLogger.info("KSGiant Enabled. Beware The Giants!")
  }

  override def onDisable(): Unit ={
    stoneGiantEvent.disableThread()
    try {
      System.out.println("Waiting for stoneGiantEvent thread to end...")
      t.join()
    } catch {
      case e: InterruptedException =>
        e.printStackTrace()
    }
    Bukkit.getLogger.info("KSGiant Disabled")
  }
}
