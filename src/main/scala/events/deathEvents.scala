package events

import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.ItemStack
import org.bukkit.{Bukkit, Material, Sound}

import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.util.Random

class deathEvents extends Listener{
  var dropsTable: List[ItemStack] = Nil
  private var listSize = 0
  initItems()

  protected def initItems() {
    val dsword = new ItemStack(Material.DIAMOND_SWORD, 1)
    var im = dsword.getItemMeta
    im.setDisplayName("Giant Slaying Blade")
    dsword.setItemMeta(im)
    dsword.setItemMeta(im)
    dsword.addEnchantment(Enchantment.DAMAGE_ALL, 4)
    dsword.addEnchantment(Enchantment.DURABILITY, 3)
    val darmor = new ItemStack(Material.DIAMOND_CHESTPLATE, 1)
    im = darmor.getItemMeta
    im.setDisplayName("Giant's Armor")
    darmor.setItemMeta(im)
    darmor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
    darmor.addEnchantment(Enchantment.DURABILITY, 3)
    val dboots = new ItemStack(Material.DIAMOND_BOOTS, 1)
    im = darmor.getItemMeta
    im.setDisplayName("Spiked Boots")
    val lore = List(org.bukkit.ChatColor.DARK_PURPLE + "Boots infused with gripping iron spikes")
    im.setLore(lore.asJava)
    dboots.setItemMeta(im)
    darmor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
    dboots.addEnchantment(Enchantment.DURABILITY, 3)
    val diamond = new ItemStack(Material.DIAMOND, 4)
    val crystals = new ItemStack(Material.PRISMARINE_CRYSTALS, 6)
    val bow = new ItemStack(Material.BOW, 1)
    im = darmor.getItemMeta
    im.setDisplayName("Adamantium Bow")
    bow.setItemMeta(im)
    bow.addEnchantment(Enchantment.ARROW_DAMAGE, 4)
    bow.addEnchantment(Enchantment.DURABILITY, 3)
    val shards = new ItemStack(Material.PRISMARINE_SHARD, 10)
    val netherstar = new ItemStack(Material.NETHER_STAR, 1)
    dropsTable = List(dsword,shards,diamond,dboots,darmor,crystals,diamond,bow,shards,diamond,netherstar)
    listSize = dropsTable.size
  }

  @EventHandler private def giantDeath(event: EntityDeathEvent) {
    if (event.getEntityType.equals(EntityType.GIANT)) {
      val r = new Random(System.currentTimeMillis)
      Bukkit.getServer.getWorld(event.getEntity.getWorld.getUID).playSound(event.getEntity.getLocation, Sound.ENTITY_BLAZE_DEATH, 6.0F, 0.5F)
      var index = r.nextInt(listSize)
      if (index == listSize - 1) {
        index = r.nextInt(listSize)
      }
      if (index == listSize - 1) {
        index = r.nextInt(listSize)
      }
      Bukkit.getServer.getWorld(event.getEntity.getWorld.getUID).dropItem(event.getEntity.getLocation, dropsTable(index))
      val potion = new ItemStack(Material.POTION, 1, 8229.toShort)
      Bukkit.getServer.getWorld(event.getEntity.getWorld.getUID).dropItem(event.getEntity.getLocation, potion)
      event.setDroppedExp(200)
    }
  }
}
