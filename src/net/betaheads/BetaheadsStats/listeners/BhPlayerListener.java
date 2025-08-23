package net.betaheads.BetaheadsStats.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
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

      BlockStatsManager.removeUserRecords(user.id);
      ActivityStatsManager.removeUserRecords(user.id);
      UserManager.removeUser(username);
    });
  }

  @Override
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    Entity entityInteracted = event.getRightClicked();

    if (entityInteracted instanceof Sheep) {

      Sheep sheep = (Sheep) entityInteracted;

      ItemStack handItem = event.getPlayer().getItemInHand();

      if (!sheep.isSheared() && handItem.getType() == Material.SHEARS) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(BetaheadsStats.plugin, () -> {
          Player player = event.getPlayer();
          String username = player.getName();

          User user = UserManager.getUser(username);

          ActivityStatsManager.handleUserActivity(user.id, Activity.SHEAR_SHEEP, ActivityType.COMMON);
        });
      }
    }
  }

  @Override
  public void onPlayerFish(PlayerFishEvent event) {
    if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
      Player player = event.getPlayer();
      String username = player.getName();

      User user = UserManager.getUser(username);

      ActivityStatsManager.handleUserActivity(user.id, Activity.FISH_CAUGHT, ActivityType.COMMON);
    }
  }
}
