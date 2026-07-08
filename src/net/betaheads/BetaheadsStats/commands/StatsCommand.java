package net.betaheads.BetaheadsStats.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.betaheads.BetaheadsStats.ActivityStatsManager;
import net.betaheads.BetaheadsStats.BetaheadsStats;
import net.betaheads.BetaheadsStats.BlockStatsManager;
import net.betaheads.BetaheadsStats.UserManager;
import net.betaheads.BetaheadsStats.entities.ActivityStat;
import net.betaheads.BetaheadsStats.entities.BlockStat;
import net.betaheads.BetaheadsStats.entities.User;
import net.betaheads.BetaheadsStats.entities.enums.Activity;
import net.betaheads.BetaheadsStats.entities.enums.ActivityType;
import net.betaheads.BetaheadsStats.entities.enums.BlockAction;
import net.betaheads.utils.Utils;

public class StatsCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return true;
    }

    Bukkit.getScheduler().scheduleAsyncDelayedTask(BetaheadsStats.plugin, () -> {
      String firstArg = "";
      try {
        firstArg = args[0];
      } catch (Exception e) {
      }

      if (firstArg.equalsIgnoreCase("a")) {
        showActivityStats(sender, args);
      } else {
        showBlockStats(sender, args);
      }
    });

    return true;
  }

  private void showBlockStats(CommandSender sender, String[] args) {
    int pageSize = 9;

    int page = 1;
    try {
      page = Integer.parseInt(args[0]);
    } catch (Exception e) {
    }

    Player player = (Player) sender;
    String username = player.getName();

    User user = UserManager.getUser(username);

    ConcurrentHashMap<String, BlockStat> stats = BlockStatsManager.getUserBlockStats(user.id);

    int colWidth = getMaxCountLength(stats.values());

    ArrayList<String[]> statsGrouped = groupByBlock(stats);

    int pages = statsGrouped.size() / pageSize + ((statsGrouped.size() % pageSize == 0) ? 0 : 1);

    pages++; // first page for total playtime

    page = pages < page ? pages : page;
    page = page < 1 ? 1 : page;

    List<String[]> statsRows = null;

    if (page == 1) {
      player.sendMessage(ChatColor.GOLD + "--- Betaheads stats ---");
      player.sendMessage(ChatColor.GOLD + "Total playtime: " + Utils.formatMillis(user.getTotalPlayedTime()));
      player.sendMessage(
          ChatColor.GOLD + "Current session playtime: " + Utils.formatMillis(user.getCurrentSessionPlayTime()));
      player.sendMessage(ChatColor.GOLD + "Logins count: " + user.login_count);
      player.sendMessage(ChatColor.GOLD + "First login: " + Utils.formatDate(user.first_login_at));
      player.sendMessage(ChatColor.GOLD + "Last login: " + Utils.formatDate(user.last_login_at));

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
  }

  private ArrayList<String[]> groupByBlock(ConcurrentHashMap<String, BlockStat> stats) {
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

  private void showActivityStats(CommandSender sender, String[] args) {
    int pageSize = 9;

    Player player = (Player) sender;
    String username = player.getName();

    User user = UserManager.getUser(username);

    ConcurrentHashMap<String, ActivityStat> stats = ActivityStatsManager.getUserActivityStats(user.id);

    if (stats.isEmpty()) {
      player.sendMessage(ChatColor.GOLD + "You don't have any activity statistics yet.");
      return;
    }

    ArrayList<String> lines = groupByActivityType(stats);

    int pages = lines.size() / pageSize + ((lines.size() % pageSize == 0) ? 0 : 1);

    int page = 1;
    try {
      page = Integer.parseInt(args[1]);
    } catch (Exception e) {
    }

    page = pages < page ? pages : page;
    page = page < 1 ? 1 : page;

    int startIndex = pageSize * (page - 1);
    int endIndex = startIndex + pageSize;
    endIndex = endIndex > lines.size() ? lines.size() : endIndex;

    List<String> statsRows = lines.subList(startIndex, endIndex);

    for (String row : statsRows) {
      player.sendMessage(row);
    }

    player.sendMessage(
        ChatColor.GOLD + "Page " + page + "/" + pages + " '/stats a <page number>' to move through pages.");
  }

  private ArrayList<String> groupByActivityType(ConcurrentHashMap<String, ActivityStat> stats) {
    ActivityType[] typesOrder = {
        ActivityType.COMMON,
        ActivityType.HOSTILE_MOB_KILL,
        ActivityType.PEACEFUL_MOB_KILL,
        ActivityType.PLAYER_KILL
    };

    ArrayList<String> lines = new ArrayList<>();

    for (ActivityType type : typesOrder) {
      ArrayList<String[]> rows = new ArrayList<>();

      for (ActivityStat stat : stats.values()) {
        if (stat.type.equals(type.toString())) {
          String activityString = getReadableActivityString(Activity.valueOf(stat.activity));

          rows.add(new String[] { activityString, Long.toString(stat.count) });
        }
      }

      if (rows.isEmpty()) {
        continue;
      }

      rows.sort((a, b) -> a[0].compareTo(b[0]));

      lines.add(ChatColor.GOLD + "--- " + getReadableActivityTypeString(type) + " ---");

      for (String[] row : rows) {
        lines.add(ChatColor.GOLD + row[0] + ": " + ChatColor.DARK_GREEN + row[1]);
      }
    }

    return lines;
  }

  private String getReadableActivityTypeString(ActivityType type) {
    switch (type) {
      case COMMON:
        return "Common actions";
      case HOSTILE_MOB_KILL:
        return "Hostile mob kills";
      case PEACEFUL_MOB_KILL:
        return "Peaceful mob kills";
      case PLAYER_KILL:
        return "Player kills";

      default:
        return "TYPE_NOT_FOUND";
    }
  }

  private String getReadableActivityString(Activity activity) {
    switch (activity) {
      case SHEAR_SHEEP:
        return "Sheared sheeps";
      case FISH_CAUGHT:
        return "Fish caught";
      case ZOMBIE_KILL:
        return "Zombies killed";
      case SKELETON_KILL:
        return "Skeletons killed";
      case SPIDER_KILL:
        return "Spiders killed";
      case CREEPER_KILL:
        return "Creepers killed";
      case SLIME_KILL:
        return "Slimes killed";
      case PIG_ZOMBIE_KILL:
        return "Zombie pigmen killed";
      case GHAST_KILL:
        return "Ghasts killed";
      case GIANT_KILL:
        return "Giants killed";
      case CHICKEN_KILL:
        return "Chickens killed";
      case COW_KILL:
        return "Cows killed";
      case PIG_KILL:
        return "Pigs killed";
      case SHEEP_KILL:
        return "Sheeps killed";
      case SQUID_KILL:
        return "Squids killed";
      case WOLF_KILL:
        return "Wolves killed";
      case PLAYER_KILL:
        return "Players killed";

      default:
        return "ACTIVITY_NOT_FOUND";
    }
  }
}
