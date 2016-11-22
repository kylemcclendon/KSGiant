package ksgiant

import events.{damageEvents, deathEvents, spawnEvents}
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class KSGiant extends JavaPlugin{
  private val damageEvents = new damageEvents(this)
  private val deathEvents = new deathEvents
  private val spawnEvents = new spawnEvents

  override def onEnable(): Unit ={
    val pm = getServer.getPluginManager

    pm.registerEvents(damageEvents, this)
    pm.registerEvents(deathEvents, this)
    pm.registerEvents(spawnEvents, this)
    Bukkit.getLogger.info("KSGiant Enabled. Beware The Giants!")
  }

  override def onDisable(): Unit ={
    Bukkit.getLogger.info("KSGiant Disabled")
  }
}
