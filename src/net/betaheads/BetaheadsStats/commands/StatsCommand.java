package net.betaheads.BetaheadsStats.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.betaheads.BetaheadsStats.UserManager;
import net.betaheads.BetaheadsStats.entities.User;
import net.betaheads.utils.Utils;

public class StatsCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return true;
    }

    Player player = (Player) sender;
    String username = player.getName().toLowerCase();

    User user = UserManager.getUser(username);

    player.sendMessage(ChatColor.GOLD + "--- Betaheads stats ---");
    player.sendMessage(ChatColor.GOLD + "Total playtime: " + Utils.formatMillis(user.getTotalPlayedTime()));
    player.sendMessage(
        ChatColor.GOLD + "Current session playtime: " + Utils.formatMillis(user.getCurrentSessionPlayTime()));

    return true;
  }
}
