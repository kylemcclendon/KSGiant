package ksgiant

import giant.Giant
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class KSGiant extends JavaPlugin{
  private val gi = Giant(this)

  override def onEnable(): Unit ={
    val pm = getServer.getPluginManager

    pm.registerEvents(gi, this)
    Bukkit.getLogger.info("KSGiant Enabled. Beware The Giants!")
  }

  override def onDisable(): Unit ={
    Bukkit.getLogger.info("KSGiant Disabled")
  }
}
