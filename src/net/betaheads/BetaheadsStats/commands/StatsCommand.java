package net.betaheads.BetaheadsStats.commands;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.betaheads.BetaheadsStats.BlockStatsManager;
import net.betaheads.BetaheadsStats.UserManager;
import net.betaheads.BetaheadsStats.entities.BlockStat;
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

    HashMap<String, BlockStat> stats = BlockStatsManager.getUserBlockStats(user.id);

    player.sendMessage(ChatColor.GOLD + "--- Betaheads stats ---");
    player.sendMessage(ChatColor.GOLD + "Total playtime: " + Utils.formatMillis(user.getTotalPlayedTime()));
    player.sendMessage(
        ChatColor.GOLD + "Current session playtime: " + Utils.formatMillis(user.getCurrentSessionPlayTime()));

    for (BlockStat stat : stats.values()) {
      String blockName = Utils.toReadableName(stat.block);

      player.sendMessage(ChatColor.GOLD + stat.action + " " + blockName + " " + Long.toString(stat.count));
    }

    return true;
  }
}
