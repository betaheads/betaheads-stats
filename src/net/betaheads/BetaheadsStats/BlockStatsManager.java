package net.betaheads.BetaheadsStats;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;

import net.betaheads.BetaheadsStats.entities.BlockStat;
import net.betaheads.BetaheadsStats.entities.enums.BlockAction;
import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.entities.BlockStatEntity;

public class BlockStatsManager {
  private final static HashMap<Long, HashMap<String, BlockStat>> blockStatsMap = new HashMap<>();

  public static void addUserRecords(Long userId) {
    final ArrayList<BlockStat> userBlockStats = Repository.getUserBlockStats(userId);
    HashMap<String, BlockStat> userBlockStatsMap = new HashMap<>();

    for (BlockStat blockStat : userBlockStats) {
      String key = buildMapKey(blockStat.action, blockStat.block);

      userBlockStatsMap.put(key, blockStat);
    }

    blockStatsMap.put(userId, userBlockStatsMap);
  }

  public static HashMap<String, BlockStat> getUserBlockStats(long userId) {
    return blockStatsMap.get(userId);
  }

  public static void handleUserAction(long userId, BlockAction action, Material material) {
    HashMap<String, BlockStat> userStat = blockStatsMap.get(userId);

    String blockStatKey = buildMapKey(action.toString(), material.toString());

    BlockStat blockStat = userStat.get(blockStatKey);

    if (blockStat == null) {
      blockStat = new BlockStat();

      blockStat.user_id = userId;
      blockStat.action = action.toString();
      blockStat.block = material.toString();
      blockStat.count = 1;

      Long id = blockStat.createDbData();

      blockStat.id = id;

      userStat.put(blockStatKey, blockStat);
    } else {
      blockStat.increaseCount();
    }
  }

  public static void removeUserRecords(long userId) {
    ArrayList<BlockStatEntity> stats = new ArrayList<>();

    for (BlockStat blockStat : blockStatsMap.get(userId).values()) {
      stats.add(blockStat);
    }

    Repository.updateBatchBlockStatsCounts(stats);

    blockStatsMap.remove(userId);
  }

  public static void saveAllCounts() {
    ArrayList<BlockStatEntity> stats = new ArrayList<>();

    for (HashMap<String, BlockStat> userStats : blockStatsMap.values()) {
      for (BlockStat stat : userStats.values()) {
        stats.add(stat);
      }
    }

    Repository.updateBatchBlockStatsCounts(stats);
  };

  private static String buildMapKey(String action, String material) {
    return action + ":" + material;
  }
}
