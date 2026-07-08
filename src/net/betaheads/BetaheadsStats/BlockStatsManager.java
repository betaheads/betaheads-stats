package net.betaheads.BetaheadsStats;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;

import net.betaheads.BetaheadsStats.entities.BlockStat;
import net.betaheads.BetaheadsStats.entities.enums.BlockAction;
import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.entities.BlockStatEntity;

public class BlockStatsManager {
  private final static ConcurrentHashMap<Long, ConcurrentHashMap<String, BlockStat>> blockStatsMap = new ConcurrentHashMap<>();

  public static void addUserRecords(Long userId) {
    final ArrayList<BlockStat> userBlockStats = Repository.getUserBlockStats(userId);
    ConcurrentHashMap<String, BlockStat> userBlockStatsMap = new ConcurrentHashMap<>();

    for (BlockStat blockStat : userBlockStats) {
      String key = buildMapKey(blockStat.action, blockStat.block);

      userBlockStatsMap.put(key, blockStat);
    }

    blockStatsMap.put(userId, userBlockStatsMap);
  }

  public static ConcurrentHashMap<String, BlockStat> getUserBlockStats(long userId) {
    return blockStatsMap.get(userId);
  }

  public static void handleUserAction(long userId, BlockAction action, Material material) {
    ConcurrentHashMap<String, BlockStat> userStat = blockStatsMap.get(userId);

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

    for (ConcurrentHashMap<String, BlockStat> userStats : blockStatsMap.values()) {
      for (BlockStat stat : userStats.values()) {
        stats.add(stat);
      }
    }

    Repository.updateBatchBlockStatsCounts(stats);
  };

  public static String buildMapKey(String action, String material) {
    return action + ":" + material;
  }
}
