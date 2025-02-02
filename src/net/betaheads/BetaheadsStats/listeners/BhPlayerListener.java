package net.betaheads.BetaheadsStats.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import net.betaheads.BetaheadsStats.BetaheadsStats;
import net.betaheads.BetaheadsStats.UserManager;

public class BhPlayerListener extends PlayerListener {
  @Override
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    String username = player.getName().toLowerCase();

    Bukkit.getScheduler().scheduleAsyncDelayedTask(BetaheadsStats.plugin, () -> {
      UserManager.addUser(username);
    });
  }

  @Override
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    String username = player.getName().toLowerCase();

    Bukkit.getScheduler().scheduleAsyncDelayedTask(BetaheadsStats.plugin, () -> {
      UserManager.removeUser(username);
    });
  }
}
