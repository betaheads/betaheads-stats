package net.betaheads.BetaheadsStats.listeners;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.painting.PaintingPlaceEvent;

import net.betaheads.BetaheadsStats.ActivityStatsManager;
import net.betaheads.BetaheadsStats.UserManager;
import net.betaheads.BetaheadsStats.entities.User;
import net.betaheads.BetaheadsStats.entities.enums.Activity;
import net.betaheads.BetaheadsStats.entities.enums.ActivityType;

public class BhEntityListener extends EntityListener {
  @Override
  public void onEntityDeath(EntityDeathEvent event) {
    Entity entity = event.getEntity();

    EntityDamageEvent lastDamage = entity.getLastDamageCause();

    if (entity instanceof Player) {
      Player victim = (Player) entity;

      recordActivity(victim.getName(), getDeathActivity(lastDamage), ActivityType.DEATH, 1);
    }

    if (!(lastDamage instanceof EntityDamageByEntityEvent)) {
      return;
    }

    Entity damager = ((EntityDamageByEntityEvent) lastDamage).getDamager();

    if (!(damager instanceof Player)) {
      return;
    }

    Activity activity = getKillActivity(entity);

    if (activity == null) {
      return;
    }

    ActivityType type = getKillActivityType(activity);

    Player killer = (Player) damager;

    recordActivity(killer.getName(), activity, type, 1);
  }

  @Override
  public void onEntityDamage(EntityDamageEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Entity entity = event.getEntity();
    long damage = event.getDamage();

    String victimName = entity instanceof Player ? ((Player) entity).getName() : null;

    String damagerName = null;
    boolean isArrowHit = false;

    if (event instanceof EntityDamageByEntityEvent) {
      Entity damager = ((EntityDamageByEntityEvent) event).getDamager();

      if (damager instanceof Player) {
        damagerName = ((Player) damager).getName();
        isArrowHit = event instanceof EntityDamageByProjectileEvent;
      }
    }

    if (victimName == null && damagerName == null) { // no players involved, don't even schedule a task
      return;
    }

    if (victimName != null) {
      recordActivity(victimName, Activity.DAMAGE_TAKEN, ActivityType.COMMON, damage);
    }

    if (damagerName != null) {
      recordActivity(damagerName, Activity.DAMAGE_DEALT, ActivityType.COMMON, damage);

      if (isArrowHit) {
        recordActivity(damagerName, Activity.ARROW_HITS, ActivityType.COMMON, 1);
      }
    }
  }

  @Override
  public void onPaintingPlace(PaintingPlaceEvent event) {
    Player player = event.getPlayer();

    if (player == null) {
      return;
    }

    recordActivity(player.getName(), Activity.PAINTINGS_PLACED, ActivityType.COMMON, 1);
  }

  // pure in-memory increment, no async task needed
  private void recordActivity(String username, Activity activity, ActivityType type, long amount) {
    User user = UserManager.getUser(username);

    if (user == null) { // not loaded yet or already quit
      return;
    }

    ActivityStatsManager.handleUserActivity(user.id, activity, type, amount);
  }

  private Activity getDeathActivity(EntityDamageEvent lastDamage) {
    if (lastDamage == null) {
      return Activity.DEATH_OTHER;
    }

    switch (lastDamage.getCause()) {
      case FALL:
        return Activity.DEATH_FALL;
      case DROWNING:
        return Activity.DEATH_DROWNING;
      case LAVA:
        return Activity.DEATH_LAVA;
      case FIRE:
      case FIRE_TICK:
        return Activity.DEATH_FIRE;
      case BLOCK_EXPLOSION:
      case ENTITY_EXPLOSION:
        return Activity.DEATH_EXPLOSION;
      case VOID:
        return Activity.DEATH_VOID;
      case SUFFOCATION:
        return Activity.DEATH_SUFFOCATION;
      case LIGHTNING:
        return Activity.DEATH_LIGHTNING;
      case CONTACT:
        return Activity.DEATH_CACTUS;

      default:
        if (lastDamage instanceof EntityDamageByEntityEvent) {
          Entity damager = ((EntityDamageByEntityEvent) lastDamage).getDamager();

          return damager instanceof Player ? Activity.DEATH_PLAYER : Activity.DEATH_MOB;
        }

        return Activity.DEATH_OTHER;
    }
  }

  private Activity getKillActivity(Entity entity) {
    if (entity instanceof Player) {
      return Activity.PLAYER_KILL;
    }

    if (entity instanceof PigZombie) {
      return Activity.PIG_ZOMBIE_KILL;
    }

    if (entity instanceof Zombie) {
      return Activity.ZOMBIE_KILL;
    }

    if (entity instanceof Skeleton) {
      return Activity.SKELETON_KILL;
    }

    if (entity instanceof Spider) {
      return Activity.SPIDER_KILL;
    }

    if (entity instanceof Creeper) {
      return Activity.CREEPER_KILL;
    }

    if (entity instanceof Slime) {
      return Activity.SLIME_KILL;
    }

    if (entity instanceof Ghast) {
      return Activity.GHAST_KILL;
    }

    if (entity instanceof Giant) {
      return Activity.GIANT_KILL;
    }

    if (entity instanceof Chicken) {
      return Activity.CHICKEN_KILL;
    }

    if (entity instanceof Cow) {
      return Activity.COW_KILL;
    }

    if (entity instanceof Pig) {
      return Activity.PIG_KILL;
    }

    if (entity instanceof Sheep) {
      return Activity.SHEEP_KILL;
    }

    if (entity instanceof Squid) {
      return Activity.SQUID_KILL;
    }

    if (entity instanceof Wolf) {
      return Activity.WOLF_KILL;
    }

    return null;
  }

  private ActivityType getKillActivityType(Activity activity) {
    switch (activity) {
      case PLAYER_KILL:
        return ActivityType.PLAYER_KILL;
      case ZOMBIE_KILL:
      case SKELETON_KILL:
      case SPIDER_KILL:
      case CREEPER_KILL:
      case SLIME_KILL:
      case PIG_ZOMBIE_KILL:
      case GHAST_KILL:
      case GIANT_KILL:
        return ActivityType.HOSTILE_MOB_KILL;

      default:
        return ActivityType.PEACEFUL_MOB_KILL;
    }
  }
}
