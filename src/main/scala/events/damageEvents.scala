package events

import ksgiant.KSGiant
import net.minecraft.server.v1_11_R1.EnumMoveType
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftGiant
import org.bukkit.entity.{Fireball, _}
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.{EntityDamageByEntityEvent, EntityDamageEvent, EntityExplodeEvent}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.plugin.Plugin
import org.bukkit.potion.{PotionEffect, PotionEffectType}
import org.bukkit.projectiles.ProjectileSource
import org.bukkit.util.Vector
import org.bukkit.{Bukkit, Location, Material, Sound}

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.util.Random

case class damageEvents(instance: KSGiant) extends Listener {
  var plugin: Plugin = instance

  @EventHandler
  private def playerHurtGiant(event: EntityDamageByEntityEvent) {
    //Check if entity getting hurt is Giant
    if(event.getEntityType.equals(EntityType.GIANT)){
      if(event.getDamager.isInstanceOf[Player]) {
        //Attacker is a melee Player
        event.setDamage(event.getDamage * 0.75D) //Three-Quarter damage to entity to nerf melee

        val endDamage = event.getFinalDamage
        event.setCancelled(true)
        event.getEntity.asInstanceOf[CraftGiant].damage(endDamage)
        val l = event.getEntity.getLocation
        event.getEntity.getWorld.playEffect(l, org.bukkit.Effect.STEP_SOUND, 152)
        val g = event.getEntity.asInstanceOf[CraftGiant]
        g.setTarget(event.getDamager.asInstanceOf[LivingEntity])

        val random = new Random(System.currentTimeMillis)
        val next = random.nextInt(15)
        val sound = random.nextInt(3)
        if (sound == 2) {
          Bukkit.getWorld(event.getEntity.getWorld.getUID).playSound(l, Sound.ENTITY_ENDERMEN_SCREAM, 6.0F, 0.1F)
        }
        if (!event.getEntity.isDead) {
          giantAttackSelector(event.getEntity.asInstanceOf[CraftGiant], event.getDamager)

          if (next == 3) {
            event.getEntity.getWorld.spawnEntity(l, EntityType.ZOMBIE).asInstanceOf[Zombie].setBaby(true)
            event.getEntity.getWorld.spawnEntity(l, EntityType.ZOMBIE).asInstanceOf[Zombie].setBaby(true)
            event.getEntity.getWorld.spawnEntity(l, EntityType.ZOMBIE).asInstanceOf[Zombie].setBaby(true)
          }
          else if(next == 5){
            val pz1 = event.getEntity.getWorld.spawnEntity(l, EntityType.PIG_ZOMBIE).asInstanceOf[PigZombie]
            val pz2 = event.getEntity.getWorld.spawnEntity(l, EntityType.PIG_ZOMBIE).asInstanceOf[PigZombie]
            pz1.setAnger(32767)
            pz1.setTarget(event.getDamager.asInstanceOf[Player])
            pz2.setAnger(32767)
            pz2.setTarget(event.getDamager.asInstanceOf[Player])
          }
        }
      }
      else if(event.getDamager.getType.equals(EntityType.ARROW) && event.getDamager.asInstanceOf[Arrow].getShooter.isInstanceOf[Player]){
        //Attacker is a bow Player
        event.setDamage(event.getDamage / 4.0D) //Quarter damage to entity to nerf bows

        val e = event.getEntity
        var eloc = e.getLocation()
        e.getWorld.playSound(eloc,Sound.ENTITY_POLAR_BEAR_DEATH, 10, 0.1F)
        eloc = eloc.add(0,10,0)
        val p = event.getDamager.asInstanceOf[Arrow].getShooter.asInstanceOf[Player]
        val loc = p.getLocation

        val fireballChance = Math.random()*2

        if(fireballChance.toInt == 1) {
          val fireball = eloc.getWorld.spawn(eloc, classOf[Fireball])
          fireball.setIsIncendiary(false)
          fireball.setYield(3)
          fireball.setShooter(e.asInstanceOf[ProjectileSource])

          val vector = loc.toVector().subtract(eloc.toVector())
          fireball.setDirection(vector.normalize().multiply(0.0001))
        }
      }
    else{
      //Prevent Other Damage From Giant Attacks
      if(event.getDamager.isInstanceOf[Fireball]){
        val f = event.getDamager.asInstanceOf[Fireball]
        if(f.getShooter.isInstanceOf[CraftGiant]){
          event.setCancelled(true)
        }
      }
    }
    }
  }

  private def giantAttackSelector(giant: CraftGiant, damager: Entity) {
    val rand = new Random(System.currentTimeMillis)
    val attack = rand.nextInt(5)
    attack match {
      case 0 =>
      case 1 =>
        giantLeap(giant.getLocation, giant)
      case 2 =>
        giantRoar(giant.getLocation)
      case 3 =>
        giantStomp(giant.getLocation, giant)
      case 4 =>
        giantToss(giant, damager)
    }
  }

  private def giantLeap(l: Location, giant: CraftGiant) {
    giant.setVelocity(new Vector(0, 0, 0))
    l.getWorld.playSound(l, Sound.ENTITY_ENDERDRAGON_FLAP, 6.0F, 0.1F)
    giant.getHandle.move(EnumMoveType.SELF, .0D, 20.0D, 0.0D)
  }

  private def giantStomp(l: Location, giant: CraftGiant) {
    val players = Bukkit.getServer.getWorld(l.getWorld.getUID).getPlayers.asScala
    var close: List[Player] = Nil

    l.getWorld.playSound(l, Sound.ENTITY_ENDERDRAGON_GROWL, 6.0F, 1.5F)
    Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
      def run() {
        for (p <- players) {
          val distance = getDistance(l, p.getLocation)
          if (distance <= 7.5D) {
            close = close.::(p)
          }
        }
        l.getWorld.createExplosion(l.getX, l.getY, l.getZ, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX + 3.75D, l.getY, l.getZ, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 3.75D, l.getY, l.getZ, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX, l.getY, l.getZ + 3.75D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX, l.getY, l.getZ - 3.75D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX + 3.75D, l.getY, l.getZ + 3.75D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 3.75D, l.getY, l.getZ - 3.75D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 3.75D, l.getY, l.getZ + 3.75D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX + 3.75D, l.getY, l.getZ - 3.75D, 0.0F, false, false)
        for (p <- close) {
          val distanceMultiplier = getDistance(l, p.getLocation)/7.5
          if(p.isBlocking){
            p.damage(75D-75D*distanceMultiplier, giant)
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1))
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,100,0))
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1))
            pushAway(p, 2.0D, giant)
          }
          else {
            p.damage(15.0-15.0*distanceMultiplier, giant)
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1))
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0))
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 2))
            pushAway(p, 3.0D, giant)
          }
        }
      }
    }, 25L)
  }

  private def giantRoar(l: Location) {
    val players = Bukkit.getServer.getWorld(l.getWorld.getUID).getPlayers.asScala
    for (p <- players) if (getDistance(l, p.getLocation) <= 20.0D) {
      l.getWorld.playSound(l, Sound.ENTITY_ENDERDRAGON_GROWL, 6.0F, 0.7F)
      p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1))
      p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 50, 0))
      p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 50, 1))
    }
  }

  private def giantToss(giant: CraftGiant, damager: Entity) {
    damager match {
      case player: Player =>
        if(getDistance(player.getLocation, giant.getLocation) < 5.0D){
          if ((player.getInventory.getBoots != null) && (player.getInventory.getBoots.getType eq Material.DIAMOND_BOOTS) && (player.getInventory.getBoots.getItemMeta != null) && (player.getInventory.getBoots.getItemMeta.getDisplayName != null) && player.getInventory.getBoots.getItemMeta.getDisplayName.equals("Spiked Boots"))
            player.setVelocity(new Vector(0, 1, 0))
          else
            player.setVelocity(new Vector(0.0D, 1.5D, 0.0D))
        }
    }
  }

  private def giantShockwave(l: Location, giant: CraftGiant): Unit ={
    val players = Bukkit.getServer.getWorld(l.getWorld.getUID).getPlayers.asScala
    var close: List[Player] = Nil
    giant.getWorld.createExplosion(l.getX, l.getY, l.getZ, 0.0F, false, false)
    //Explosion Visual Effect Inner Radius
    Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
      def run() {
        for (p <- players) {
          val distance = getDistance(l, p.getLocation)
          if (distance <= 10.0D) {
            close = close.::(p)
          }
        }
        giant.getWorld.createExplosion(l.getX + 3.33D, l.getY, l.getZ, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX - 3.33D, l.getY, l.getZ, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX, l.getY, l.getZ + 3.33D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX, l.getY, l.getZ - 3.33D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX + 3.33D, l.getY, l.getZ + 3.33D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX - 3.33D, l.getY, l.getZ - 3.33D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX - 3.33D, l.getY, l.getZ + 3.33D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX + 3.33D, l.getY, l.getZ - 3.33D, 0.0F, false, false)

        //Hurt Players And Push Them Away
        for (p <- close) {
          val distanceMultiplier = getDistance(l, p.getLocation)/10
          println(distanceMultiplier)
          if(p.isBlocking){
            p.damage(120-(120*distanceMultiplier), giant)
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1))
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0))
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1))
            pushAway(p, 7.0D, giant)
          }
          else {
            p.damage(30-(30*distanceMultiplier), giant)
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1))
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0))
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 2))
            pushAway(p, 7.0D, giant)
          }
        }
      }
    }, 3L)
    //Explosion Visual Effect Middle Radius
    Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
      def run() {
        giant.getWorld.createExplosion(l.getX + 6.66D, l.getY, l.getZ, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX - 6.66D, l.getY, l.getZ, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX, l.getY, l.getZ + 6.66D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX, l.getY, l.getZ - 6.66D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX + 6.66D, l.getY, l.getZ + 6.66D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX - 6.66D, l.getY, l.getZ - 6.66D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX + 6.66D, l.getY, l.getZ - 6.66D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX - 6.66D, l.getY, l.getZ + 6.66D, 0.0F, false, false)
      }
    }, 6L)
    //Explosion Visual Effect Outer Radius
    Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
      def run() {
        giant.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX, l.getY, l.getZ + 10.0D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX, l.getY, l.getZ - 10.0D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ - 10.0D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ - 10.0D, 0.0F, false, false)
//        giant.getWorld.createExplosion(l.getX + 15.0D, l.getY, l.getZ + 10.0D, 0.0F, false, false)
//        giant.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ + 15.0D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ - 10.0D, 0.0F, false, false)
        giant.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ - 10.0D, 0.0F, false, false)
//        giant.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ + 15.0D, 0.0F, false, false)
//        giant.getWorld.createExplosion(l.getX - 15.0D, l.getY, l.getZ + 10.0D, 0.0F, false, false)
      }
    }, 9L)


  }

  @EventHandler
  private def giantFireball(event: EntityExplodeEvent ): Unit ={
    if(event.getEntityType.equals(EntityType.FIREBALL)){
      val f = event.getEntity.asInstanceOf[Fireball]
      val floc = event.getLocation
      if(f.getShooter.isInstanceOf[CraftGiant]){
        val babies = Math.floor(Math.random()*5)
        if(babies == 1){
          f.getWorld.spawnEntity(floc, EntityType.ZOMBIE).asInstanceOf[Zombie].setBaby(true)
          f.getWorld.spawnEntity(floc, EntityType.ZOMBIE).asInstanceOf[Zombie].setBaby(true)
          f.getWorld.spawnEntity(floc, EntityType.ZOMBIE).asInstanceOf[Zombie].setBaby(true)
        }
        else if(babies == 2){
          val pz1 = event.getEntity.getWorld.spawnEntity(floc, EntityType.PIG_ZOMBIE).asInstanceOf[PigZombie]
          val pz2 = event.getEntity.getWorld.spawnEntity(floc, EntityType.PIG_ZOMBIE).asInstanceOf[PigZombie]
          pz1.setAnger(32767)
          pz2.setAnger(32767)
        }
        event.setCancelled(true)
      }
    }
  }

  @EventHandler
  @throws[InterruptedException]
  private def giantFall(event: EntityDamageEvent) {
    event.getEntityType match {
      case EntityType.ARMOR_STAND =>
        if(event.getCause.equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
          event.setCancelled(true)
        }
      case EntityType.GIANT =>
        event.getCause match{
          case DamageCause.FIRE | DamageCause.FIRE_TICK | DamageCause.POISON | DamageCause.HOT_FLOOR | DamageCause.DROWNING =>
            event.setCancelled(true)
          case DamageCause.FALL =>
            event.setDamage(0)
            val l = event.getEntity.getLocation
            giantShockwave(l, event.getEntity.asInstanceOf[CraftGiant])
          case DamageCause.CONTACT =>
            findAndCrushCacti(event.getEntity.getLocation)
          case DamageCause.LIGHTNING =>
            event.getEntity.asInstanceOf[CraftGiant].addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 500, 2))
          case _ =>
        }
      case _ =>
    }
  }

  private def findAndCrushCacti(l: Location): Unit ={
    var location = l
    val locX = l.getX
    val locY = l.getY
    val locZ = l.getZ

    var cactusLocsBuffer = new ListBuffer[Location]()

    for(x <- -3 to 3){
      for(z <- -3 to 3){
        if(Bukkit.getWorld(l.getWorld.getUID).getBlockAt(locX.toInt+x, locY.toInt, locZ.toInt+z).getType.equals(Material.CACTUS)){
          cactusLocsBuffer += new Location(l.getWorld, locX.toInt+x, locY, locZ.toInt+z)
        }
      }
    }

    for( loc <- cactusLocsBuffer){
      loc.getBlock.breakNaturally()
    }
  }

  private def getDistance(ent1: Location, ent2: Location) = {
    val x2 = Math.pow(ent2.getX - ent1.getX, 2.0D)
    val y2 = Math.pow(ent2.getY - ent1.getY, 2.0D)
    val z2 = Math.pow(ent2.getZ - ent1.getZ, 2.0D)
    Math.sqrt(x2 + y2 + z2)
  }

  private def pushAway(player: Player, speed: Double, entity: Entity) {
    if ((player.getInventory.getBoots != null) && (player.getInventory.getBoots.getType eq Material.DIAMOND_BOOTS) && (player.getInventory.getBoots.getItemMeta != null) && (player.getInventory.getBoots.getItemMeta.getDisplayName != null) && player.getInventory.getBoots.getItemMeta.getDisplayName.equals("Spiked Boots") && (player.getInventory.getBoots.getItemMeta.getLore != null) && player.getInventory.getBoots.getItemMeta.getLore.contains(org.bukkit.ChatColor.DARK_PURPLE + "Boots infused with gripping iron spikes")) {
      return
    }
    val unitVector = player.getLocation.toVector.subtract(entity.getLocation.toVector).normalize
    player.setVelocity(unitVector.multiply(speed))
  }
}
