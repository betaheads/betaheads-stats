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

    if (userBlockStats != null) { // null on DB error, start with empty stats
      for (BlockStat blockStat : userBlockStats) {
        String key = buildMapKey(blockStat.action, blockStat.block);

        userBlockStatsMap.put(key, blockStat);
      }
    }

    blockStatsMap.put(userId, userBlockStatsMap);
  }

  public static ConcurrentHashMap<String, BlockStat> getUserBlockStats(long userId) {
    return blockStatsMap.get(userId);
  }

  // pure in-memory, safe to call from any thread; new stats are inserted
  // into the DB later by the save tasks
  public static void handleUserAction(long userId, BlockAction action, Material material) {
    ConcurrentHashMap<String, BlockStat> userStat = blockStatsMap.get(userId);

    if (userStat == null) { // user already quit
      return;
    }

    String blockStatKey = buildMapKey(action.toString(), material.toString());

    BlockStat blockStat = userStat.get(blockStatKey);

    if (blockStat == null) {
      BlockStat newStat = new BlockStat();

      newStat.id = 0; // not in DB yet
      newStat.user_id = userId;
      newStat.action = action.toString();
      newStat.block = material.toString();
      newStat.count = 0;

      BlockStat existingStat = userStat.putIfAbsent(blockStatKey, newStat);

      blockStat = existingStat == null ? newStat : existingStat;
    }

    blockStat.increaseCount();
  }

  public static void removeUserRecords(long userId) {
    ConcurrentHashMap<String, BlockStat> userStats = blockStatsMap.get(userId);

    if (userStats == null) {
      return;
    }

    ArrayList<BlockStatEntity> stats = new ArrayList<>();

    for (BlockStat blockStat : userStats.values()) {
      stats.add(blockStat);
    }

    Repository.saveBatchBlockStats(stats);

    blockStatsMap.remove(userId);
  }

  public static void saveAllCounts() {
    ArrayList<BlockStatEntity> stats = new ArrayList<>();

    for (ConcurrentHashMap<String, BlockStat> userStats : blockStatsMap.values()) {
      for (BlockStat stat : userStats.values()) {
        stats.add(stat);
      }
    }

    Repository.saveBatchBlockStats(stats);
  };

  public static String buildMapKey(String action, String material) {
    return action + ":" + material;
  }
}
