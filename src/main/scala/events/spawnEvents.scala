package events

import net.minecraft.server.v1_11_R1.EnumItemSlot
import org.bukkit.block.Biome
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftGiant
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.ItemStack
import org.bukkit.{Bukkit, Material, Sound}

import scala.util.Random

class spawnEvents extends Listener{
  @EventHandler(priority = org.bukkit.event.EventPriority.HIGH, ignoreCancelled = true)
  private def spawnGiant(event: CreatureSpawnEvent) {
    if (event.getEntityType.equals(EntityType.ZOMBIE) && event.getSpawnReason.equals(CreatureSpawnEvent.SpawnReason.NATURAL) && (event.getEntity.getLocation.getBlock.getLightFromSky == 15)) {
      val r = new Random(System.currentTimeMillis)
      val rainingModifier = if(event.getLocation.getWorld.hasStorm) 0.75 else 1

      val randRange = (rainingModifier * (event.getLocation.getBlock.getBiome match {
        case Biome.MUTATED_EXTREME_HILLS | Biome.MUTATED_EXTREME_HILLS_WITH_TREES | Biome.MUTATED_MESA | Biome.MUTATED_MESA_CLEAR_ROCK | Biome.MUTATED_MESA_ROCK | Biome.MUTATED_TAIGA_COLD | Biome.STONE_BEACH | Biome.MUTATED_SWAMPLAND | Biome.MUTATED_SAVANNA | Biome.MUTATED_SAVANNA_ROCK | Biome.MUTATED_BIRCH_FOREST =>
          10
        case Biome.EXTREME_HILLS | Biome.EXTREME_HILLS_WITH_TREES | Biome.TAIGA_COLD_HILLS | Biome.TAIGA_HILLS =>
          25
        case _ =>
          50
      })).toInt

      val next = r.nextInt(randRange)

      if (next == 5) {
        val l = event.getLocation
        val g = event.getEntity.getWorld.spawnEntity(l, EntityType.GIANT).asInstanceOf[CraftGiant]
        g.getHandle.getAttributeInstance(net.minecraft.server.v1_11_R1.GenericAttributes.maxHealth).setValue(200.0D)
        g.setHealth(200.0D)
        g.getHandle.setEquipment(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(new ItemStack(Material.LEATHER_CHESTPLATE, 1)))
        Bukkit.getWorld(event.getEntity.getWorld.getUID).playSound(l, Sound.ENTITY_ENDERDRAGON_GROWL, 6.0F, 1.0F)
        event.getEntity.remove()
      }
    }
  }
}