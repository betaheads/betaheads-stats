package net.betaheads.BetaheadsStats.listeners;

import org.bukkit.Bukkit;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import net.betaheads.BetaheadsStats.ActivityStatsManager;
import net.betaheads.BetaheadsStats.BetaheadsStats;
import net.betaheads.BetaheadsStats.UserManager;
import net.betaheads.BetaheadsStats.entities.User;
import net.betaheads.BetaheadsStats.entities.enums.Activity;
import net.betaheads.BetaheadsStats.entities.enums.ActivityType;

public class BhEntityListener extends EntityListener {
  @Override
  public void onEntityDeath(EntityDeathEvent event) {
    Entity entity = event.getEntity();

    EntityDamageEvent lastDamage = entity.getLastDamageCause();

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
    String username = killer.getName();

    Bukkit.getScheduler().scheduleAsyncDelayedTask(BetaheadsStats.plugin, () -> {
      User user = UserManager.getUser(username);

      ActivityStatsManager.handleUserActivity(user.id, activity, type);
    });
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
