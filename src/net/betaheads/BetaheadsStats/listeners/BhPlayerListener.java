package net.betaheads.BetaheadsStats.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.betaheads.BetaheadsStats.UserManager;

public class BhPlayerListener extends PlayerListener {
  private UserManager userManager = new UserManager();

  @Override
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    String username = player.getName().toLowerCase();

    userManager.addUser(username);
  }

  @Override
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    String username = player.getName().toLowerCase();

    userManager.removeUser(username);
  }
}
