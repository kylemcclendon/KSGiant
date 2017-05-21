package events

import ksgiant.KSGiant
import org.bukkit.{Bukkit, Location, Material, Sound}
import org.bukkit.entity.{Giant, LivingEntity}
import org.bukkit.scheduler.BukkitRunnable

import scala.collection.JavaConverters._
import scala.util.Random

class stoneGiantEvent(plugin: KSGiant) extends Runnable{
  var threadVar = true

  override def run(): Unit = {
    do {
      try {
        Thread.sleep(1000*60*1)
      }
      catch {
        case ex: Exception => println(ex)
      }

      Bukkit.getWorlds.asScala.foreach((world) => {
        if(world.getTime > 450 && world.getTime < 11616 && !world.isThundering) {
          world.getLivingEntities.asScala.foreach((entity) => {
            if (entity.isInstanceOf[Giant]) {
              val random = new Random(System.currentTimeMillis())
              val r = random.nextInt(100) + 1
              if (r < 6) {
                turnGiantToStone(entity, entity.getLocation)
              }
            }
          })
        }
      })
    } while(threadVar)
  }

  def turnGiantToStone(giant: LivingEntity, location: Location): Unit ={
    new BukkitRunnable {
      override def run(): Unit = {
        Bukkit.getWorld(location.getWorld.getUID).playSound(location,Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_ATTACK,10,0.5F)
        createBody(location, 0)
        createBody(location, 1)
        createBody(location, 2)
        createBody(location, 3)
        createBody(location, 4)
        createBody(location, 5)
        createBody(location, 6)
        createBody(location, 7)
        createBody(location, 8)
        createRightArm(location, 7)
        createRightArm(location, 8)
        createLeftArm(location, 7)
        createLeftArm(location, 8)
        createHeadFirstThirdLevel(location, 9)
        createHeadSecondLevel(location, 10)
        createHeadFirstThirdLevel(location, 11)
        giant.remove()
      }
    }.runTask(plugin)
  }

  def createBody(loc: Location, height: Int): Unit = {
    new Location(loc.getWorld,loc.getX,loc.getY+height,loc.getZ-1).getBlock.setType(Material.STONE)
    new Location(loc.getWorld,loc.getX+1,loc.getY+height,loc.getZ-1).getBlock.setType(Material.STONE)
    new Location(loc.getWorld,loc.getX,loc.getY+height,loc.getZ).getBlock.setType(Material.STONE)
    new Location(loc.getWorld,loc.getX+1,loc.getY+height,loc.getZ).getBlock.setType(Material.STONE)
    new Location(loc.getWorld,loc.getX,loc.getY+height,loc.getZ+1).getBlock.setType(Material.STONE)
    new Location(loc.getWorld,loc.getX+1,loc.getY+height,loc.getZ+1).getBlock.setType(Material.STONE)
  }

  def createRightArm(loc: Location, height: Int): Unit = {
    new Location(loc.getWorld, loc.getX-3,loc.getY+height,loc.getZ-3).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-2,loc.getY+height,loc.getZ-3).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-1,loc.getY+height,loc.getZ-3).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX,loc.getY+height,loc.getZ-3).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX+1,loc.getY+height,loc.getZ-3).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-3,loc.getY+height,loc.getZ-2).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-2,loc.getY+height,loc.getZ-2).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-1,loc.getY+height,loc.getZ-2).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX,loc.getY+height,loc.getZ-2).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX+1,loc.getY+height,loc.getZ-2).getBlock.setType(Material.STONE)
  }

  def createLeftArm(loc: Location, height: Int): Unit = {
    new Location(loc.getWorld, loc.getX-3,loc.getY+height,loc.getZ+2).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-2,loc.getY+height,loc.getZ+2).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-1,loc.getY+height,loc.getZ+2).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX,loc.getY+height,loc.getZ+2).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX+1,loc.getY+height,loc.getZ+2).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-3,loc.getY+height,loc.getZ+3).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-2,loc.getY+height,loc.getZ+3).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-1,loc.getY+height,loc.getZ+3).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX,loc.getY+height,loc.getZ+3).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX+1,loc.getY+height,loc.getZ+3).getBlock.setType(Material.STONE)
  }

  def createHeadFirstThirdLevel(loc: Location, height: Int): Unit = {
    new Location(loc.getWorld, loc.getX-1,loc.getY+height,loc.getZ-1).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX,loc.getY+height,loc.getZ-1).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX+1,loc.getY+height,loc.getZ-1).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-1,loc.getY+height,loc.getZ).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX,loc.getY+height,loc.getZ).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX+1,loc.getY+height,loc.getZ).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-1,loc.getY+height,loc.getZ+1).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX,loc.getY+height,loc.getZ+1).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX+1,loc.getY+height,loc.getZ+1).getBlock.setType(Material.STONE)
  }

  def createHeadSecondLevel(loc: Location, height: Int): Unit = {
    val random = new Random(System.currentTimeMillis())
    val r = random.nextInt(100)+1
    val eyeMaterial = if(r < 81){ Material.SMOOTH_BRICK } else Material.IRON_BLOCK

    val r2 = random.nextInt(100)+1
    var brainMaterial = Material.IRON_ORE
    if(r2 < 71){
      //Do Nothing
    } else if (r2 < 91){
      brainMaterial = Material.GOLD_ORE
    } else if (r2 < 96){
      brainMaterial = Material.DIAMOND_ORE
    } else if (r2 < 101) {
      brainMaterial = Material.EMERALD_ORE
    }


    new Location(loc.getWorld, loc.getX - 1, loc.getY + height, loc.getZ - 1).getBlock.setType(eyeMaterial)
    if(eyeMaterial == Material.SMOOTH_BRICK) {
      new Location(loc.getWorld, loc.getX - 1, loc.getY + height, loc.getZ - 1).getBlock.setData(3)
    }
    new Location(loc.getWorld, loc.getX,loc.getY+height,loc.getZ-1).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX+1,loc.getY+height,loc.getZ-1).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX-1,loc.getY+height,loc.getZ).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX,loc.getY+height,loc.getZ).getBlock.setType(brainMaterial)
    new Location(loc.getWorld, loc.getX+1,loc.getY+height,loc.getZ).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX - 1, loc.getY + height, loc.getZ + 1).getBlock.setType(eyeMaterial)
    if(eyeMaterial == Material.SMOOTH_BRICK) {
      new Location(loc.getWorld, loc.getX - 1, loc.getY + height, loc.getZ + 1).getBlock.setData(3)
    }
    new Location(loc.getWorld, loc.getX,loc.getY+height,loc.getZ+1).getBlock.setType(Material.STONE)
    new Location(loc.getWorld, loc.getX+1,loc.getY+height,loc.getZ+1).getBlock.setType(Material.STONE)
  }

  def disableThread(): Unit = {
    threadVar = false
  }
}
