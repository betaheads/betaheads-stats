package net.betaheads.BetaheadsStats.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Entity;

import net.betaheads.BetaheadsStats.ActivityStatsManager;
import net.betaheads.BetaheadsStats.BetaheadsStats;
import net.betaheads.BetaheadsStats.BlockStatsManager;
import net.betaheads.BetaheadsStats.UserManager;
import net.betaheads.BetaheadsStats.entities.User;
import net.betaheads.BetaheadsStats.entities.enums.Activity;
import net.betaheads.BetaheadsStats.entities.enums.ActivityType;

public class BhPlayerListener extends PlayerListener {
  @Override
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    String username = player.getName();

    Bukkit.getScheduler().scheduleAsyncDelayedTask(BetaheadsStats.plugin, () -> {
      UserManager.addUser(username);
      User user = UserManager.getUser(username);

      BlockStatsManager.addUserRecords(user.id);
      ActivityStatsManager.addUserRecords(user.id);
    });
  }

  @Override
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    String username = player.getName();

    Bukkit.getScheduler().scheduleAsyncDelayedTask(BetaheadsStats.plugin, () -> {
      User user = UserManager.getUser(username);

      if (user == null) { // join task did not manage to load the user
        return;
      }

      BlockStatsManager.removeUserRecords(user.id);
      ActivityStatsManager.removeUserRecords(user.id);
      UserManager.removeUser(username);
    });
  }

  @Override
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    Entity entityInteracted = event.getRightClicked();

    Player player = event.getPlayer();
    String username = player.getName();

    ItemStack handItem = player.getItemInHand();

    if (handItem == null) {
      return;
    }

    Material handType = handItem.getType();

    if (entityInteracted instanceof Sheep) {
      Sheep sheep = (Sheep) entityInteracted;

      if (!sheep.isSheared() && handType == Material.SHEARS) {
        recordActivity(username, Activity.SHEAR_SHEEP, ActivityType.COMMON, 1);
      }

      if (handType == Material.INK_SACK) {
        recordActivity(username, Activity.SHEEP_DYED, ActivityType.COMMON, 1);
      }
    }

    if (entityInteracted instanceof Cow && handType == Material.BUCKET) {
      recordActivity(username, Activity.COW_MILKED, ActivityType.COMMON, 1);
    }

    if (entityInteracted instanceof Wolf && handType == Material.BONE) {
      Wolf wolf = (Wolf) entityInteracted;

      if (!wolf.isTamed() && !wolf.isAngry()) {
        // taming has a random chance, so check the result on the next tick
        Bukkit.getScheduler().scheduleSyncDelayedTask(BetaheadsStats.plugin, () -> {
          if (wolf.isTamed()) {
            recordActivity(username, Activity.WOLF_TAMED, ActivityType.COMMON, 1);
          }
        }, 1L);
      }
    }
  }

  @Override
  public void onPlayerFish(PlayerFishEvent event) {
    if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
      Player player = event.getPlayer();
      String username = player.getName();

      recordActivity(username, Activity.FISH_CAUGHT, ActivityType.COMMON, 1);
    }
  }

  @Override
  public void onPlayerChat(PlayerChatEvent event) {
    recordActivity(event.getPlayer().getName(), Activity.CHAT_MESSAGES, ActivityType.COMMON, 1);
  }

  @Override
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    recordActivity(event.getPlayer().getName(), Activity.COMMANDS_USED, ActivityType.COMMON, 1);
  }

  @Override
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    int amount = event.getItem().getItemStack().getAmount();

    recordActivity(event.getPlayer().getName(), Activity.ITEMS_PICKED_UP, ActivityType.COMMON, amount);
  }

  @Override
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    int amount = event.getItemDrop().getItemStack().getAmount();

    recordActivity(event.getPlayer().getName(), Activity.ITEMS_DROPPED, ActivityType.COMMON, amount);
  }

  @Override
  public void onPlayerBucketFill(PlayerBucketFillEvent event) {
    ItemStack resultItem = event.getItemStack();

    if (resultItem == null) {
      return;
    }

    if (resultItem.getType() == Material.WATER_BUCKET) {
      recordActivity(event.getPlayer().getName(), Activity.WATER_BUCKET_FILLED, ActivityType.COMMON, 1);
    } else if (resultItem.getType() == Material.LAVA_BUCKET) {
      recordActivity(event.getPlayer().getName(), Activity.LAVA_BUCKET_FILLED, ActivityType.COMMON, 1);
    }
  }

  @Override
  public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
    Material bucket = event.getBucket();

    if (bucket == Material.WATER_BUCKET) {
      recordActivity(event.getPlayer().getName(), Activity.WATER_BUCKET_EMPTIED, ActivityType.COMMON, 1);
    } else if (bucket == Material.LAVA_BUCKET) {
      recordActivity(event.getPlayer().getName(), Activity.LAVA_BUCKET_EMPTIED, ActivityType.COMMON, 1);
    }
  }

  @Override
  public void onPlayerBedEnter(PlayerBedEnterEvent event) {
    recordActivity(event.getPlayer().getName(), Activity.NIGHTS_SLEPT, ActivityType.COMMON, 1);
  }

  @Override
  public void onPlayerPortal(PlayerPortalEvent event) {
    recordActivity(event.getPlayer().getName(), Activity.NETHER_PORTAL_USED, ActivityType.COMMON, 1);
  }

  @Override
  public void onPlayerEggThrow(PlayerEggThrowEvent event) {
    String username = event.getPlayer().getName();

    recordActivity(username, Activity.EGGS_THROWN, ActivityType.COMMON, 1);

    if (event.isHatching()) {
      recordActivity(username, Activity.CHICKENS_HATCHED, ActivityType.COMMON, event.getNumHatches());
    }
  }

  // pure in-memory increment, no async task needed
  private void recordActivity(String username, Activity activity, ActivityType type, long amount) {
    User user = UserManager.getUser(username);

    if (user == null) { // not loaded yet or already quit
      return;
    }

    ActivityStatsManager.handleUserActivity(user.id, activity, type, amount);
  }
}
