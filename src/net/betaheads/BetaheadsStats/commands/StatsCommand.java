package net.betaheads.BetaheadsStats.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.betaheads.BetaheadsStats.ActivityStatsManager;
import net.betaheads.BetaheadsStats.BlockStatsManager;
import net.betaheads.BetaheadsStats.UserManager;
import net.betaheads.BetaheadsStats.entities.ActivityStat;
import net.betaheads.BetaheadsStats.entities.BlockStat;
import net.betaheads.BetaheadsStats.entities.User;
import net.betaheads.BetaheadsStats.entities.enums.Activity;
import net.betaheads.BetaheadsStats.entities.enums.BlockAction;
import net.betaheads.utils.Utils;

public class StatsCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return true;
    }

    int page = 1;
    String firstArg = "";
    try {
      firstArg = args[0];
      page = Integer.parseInt(args[0]);
    } catch (Exception e) {
    }

    if (firstArg.equalsIgnoreCase("a")) {
      return showActivityStats(sender, cmd, label, args);
    }

    int pageSize = 9;

    Player player = (Player) sender;
    String username = player.getName();

    User user = UserManager.getUser(username);

    HashMap<String, BlockStat> stats = BlockStatsManager.getUserBlockStats(user.id);

    int colWidth = getMaxCountLength(stats.values());

    ArrayList<String[]> statsGrouped = groupByBlock(stats);

    int pages = statsGrouped.size() / pageSize + ((statsGrouped.size() % pageSize == 0) ? 0 : 1);

    pages++; // first page for total playtime

    page = pages < page ? pages : page;

    List<String[]> statsRows = null;

    if (page == 1) {
      player.sendMessage(ChatColor.GOLD + "--- Betaheads stats ---");
      player.sendMessage(ChatColor.GOLD + "Total playtime: " + Utils.formatMillis(user.getTotalPlayedTime()));
      player.sendMessage(
          ChatColor.GOLD + "Current session playtime: " + Utils.formatMillis(user.getCurrentSessionPlayTime()));

      player.sendMessage(ChatColor.GOLD + " ");
      player.sendMessage(ChatColor.GOLD + "See block statistic on next page ->");
      player.sendMessage(
          ChatColor.GOLD + "Page " + page + "/" + pages + " '/stats <page number>' to move through pages.");

      player.sendMessage(ChatColor.GOLD + " ");

      player.sendMessage(ChatColor.GOLD + "See activity statistic using '/stats a <page number>' command.");
    } else {
      int startIndex = pageSize * (page - 2);
      int endIndex = startIndex + pageSize;
      endIndex = endIndex > statsGrouped.size() ? statsGrouped.size() : endIndex;

      statsRows = statsGrouped.subList(startIndex, endIndex);

      for (String[] row : statsRows) {
        String blockName = Utils.toReadableName(row[0]);

        player.sendMessage(ChatColor.DARK_RED + "-" + String.format("%-" + colWidth + "s", row[1]).replace(" ", "_")
            + ChatColor.DARK_GREEN + " +" + String.format("%-" + colWidth + "s", row[2]).replace(" ", "_")
            + ChatColor.GOLD
            + " " + blockName);
      }

      player.sendMessage(ChatColor.GOLD + "Page " + page + "/" + pages);
    }

    return true;
  }

  private ArrayList<String[]> groupByBlock(HashMap<String, BlockStat> stats) {
    HashSet<String> blocksNames = new HashSet<>();

    for (BlockStat stat : stats.values()) {
      blocksNames.add(stat.block);
    }

    String[] sortedNames = blocksNames.toArray(new String[0]);
    Arrays.sort(sortedNames);

    ArrayList<String[]> result = new ArrayList<>();

    for (String blockName : sortedNames) {
      BlockStat breakBlockStat = stats.get(BlockStatsManager.buildMapKey(BlockAction.BREAK.toString(), blockName));
      BlockStat placeBlockStat = stats.get(BlockStatsManager.buildMapKey(BlockAction.PLACE.toString(), blockName));

      String brokenCountString = breakBlockStat == null ? "0" : Long.toString(breakBlockStat.count);
      String placedCountString = placeBlockStat == null ? "0" : Long.toString(placeBlockStat.count);

      String[] row = { blockName, brokenCountString, placedCountString };

      result.add(row);
    }

    return result;
  }

  private int getMaxCountLength(Collection<BlockStat> stats) {
    int max = 0;

    for (BlockStat blockStat : stats) {
      int length = Long.toString(blockStat.count).length();

      if (length > max) {
        max = length;
      }
    }

    return max;
  }

  private boolean showActivityStats(CommandSender sender, Command cmd, String label, String[] args) {
    int pageSize = 9;

    Player player = (Player) sender;
    String username = player.getName();

    User user = UserManager.getUser(username);

    HashMap<String, ActivityStat> stats = ActivityStatsManager.getUserActivityStats(user.id);

    Collection<ActivityStat> activityStatsCollection = stats.values();
    ArrayList<ActivityStat> activityStatsArr = new ArrayList<>();

    for (ActivityStat activityStat : activityStatsCollection) {
      activityStatsArr.add(activityStat);
    }

    int pages = activityStatsArr.size() / pageSize + ((activityStatsArr.size() % pageSize == 0) ? 0 : 1);

    int page = 1;
    try {
      page = Integer.parseInt(args[0]);
    } catch (Exception e) {
    }

    page = pages < page ? pages : page;

    List<ActivityStat> statsRows = null;

    int startIndex = pageSize * (page - 1);
    int endIndex = startIndex + pageSize;
    endIndex = endIndex > activityStatsArr.size() ? activityStatsArr.size() : endIndex;

    statsRows = activityStatsArr.subList(startIndex, endIndex);

    for (ActivityStat row : statsRows) {
      String activityString = this.getReadableActivityString(Activity.valueOf(row.activity));

      player.sendMessage(ChatColor.GOLD + activityString + ": " + ChatColor.DARK_GREEN + row.count);
    }

    player.sendMessage(ChatColor.GOLD + "Page " + page + "/" + pages);

    return true;
  }

  private String getReadableActivityString(Activity activity) {
    switch (activity) {
      case SHEAR_SHEEP:
        return "Sheared sheeps";
      case FISH_CAUGHT:
        return "Fish caught";

      default:
        return "ACTIVITY_NOT_FOUND";
    }
  }
}
