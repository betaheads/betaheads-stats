package net.betaheads.BetaheadsStats.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import net.betaheads.BetaheadsStats.ActivityStatsManager;
import net.betaheads.BetaheadsStats.BetaheadsStats;
import net.betaheads.BetaheadsStats.BlockStatsManager;
import net.betaheads.BetaheadsStats.UserManager;
import net.betaheads.BetaheadsStats.entities.User;
import net.betaheads.BetaheadsStats.entities.enums.Activity;
import net.betaheads.BetaheadsStats.entities.enums.ActivityType;
import net.betaheads.BetaheadsStats.entities.enums.BlockAction;

public class BhBlockListener extends BlockListener {
  @Override
  public void onBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    Material type = block.getType();

    if (!isValidMaterial(type)) {
      return;
    }

    Player player = event.getPlayer();
    String username = player.getName();

    Bukkit.getScheduler().scheduleAsyncDelayedTask(BetaheadsStats.plugin, () -> {
      User user = UserManager.getUser(username);

      BlockStatsManager.handleUserAction(user.id, BlockAction.BREAK, type);
    });
  }

  @Override
  public void onBlockPlace(BlockPlaceEvent event) {
    Block block = event.getBlock();
    Material type = block.getType();

    if (!isValidMaterial(type)) {
      return;
    }

    Player player = event.getPlayer();
    String username = player.getName();

    Bukkit.getScheduler().scheduleAsyncDelayedTask(BetaheadsStats.plugin, () -> {
      User user = UserManager.getUser(username);

      BlockStatsManager.handleUserAction(user.id, BlockAction.PLACE, type);
    });
  }

  @Override
  public void onSignChange(SignChangeEvent event) {
    recordActivity(event.getPlayer().getName(), Activity.SIGNS_WRITTEN, ActivityType.COMMON, 1);
  }

  @Override
  public void onBlockIgnite(BlockIgniteEvent event) {
    if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
      return;
    }

    Player player = event.getPlayer();

    if (player == null) {
      return;
    }

    recordActivity(player.getName(), Activity.FIRES_STARTED, ActivityType.COMMON, 1);
  }

  private void recordActivity(String username, Activity activity, ActivityType type, long amount) {
    Bukkit.getScheduler().scheduleAsyncDelayedTask(BetaheadsStats.plugin, () -> {
      User user = UserManager.getUser(username);

      if (user == null) { // user already quit
        return;
      }

      ActivityStatsManager.handleUserActivity(user.id, activity, type, amount);
    });
  }

  private Boolean isValidMaterial(Material material) {
    return material != Material.AIR;
  }
}
