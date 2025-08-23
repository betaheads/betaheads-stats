package net.betaheads.utils.db;

import java.util.ArrayList;

import net.betaheads.BetaheadsStats.entities.ActivityStat;
import net.betaheads.BetaheadsStats.entities.BlockStat;
import net.betaheads.utils.db.entities.ActivityStatEntity;
import net.betaheads.utils.db.entities.BlockStatEntity;
import net.betaheads.utils.db.entities.UserEntity;

public class Repository {
  private static Datasource repo = null;

  public static void setRepository(Datasource repository) {
    repo = repository;
  }

  public static void init() {
    repo.init();
  }

  public static ArrayList<String> getAllWrittenMigrations() {
    return repo.getAllWrittenMigrations();
  }

  public static void writeMigration(String migrationName) {
    repo.writeMigration(migrationName);
  }

  public static void createMigrationsTable() {
    repo.createMigrationsTable();
  }

  public static void createUsersTable() {
    repo.createUsersTable();
  }

  public static UserEntity getUser(String username) {
    return repo.getUser(username);
  }

  public static int saveUser(UserEntity user) {
    return repo.saveUser(user);
  }

  public static int[] updateUserBatch(ArrayList<UserEntity> users) {
    return repo.updateUserBatch(users);
  }

  public static int updateUser(UserEntity user) {
    return repo.updateUser(user);
  }

  public static void createBlockStatsTable() {
    repo.createBlockStatsTable();
  };

  public static ArrayList<BlockStat> getUserBlockStats(Long userId) {
    final ArrayList<BlockStatEntity> userStats = repo.getUserBlockStats(userId);

    ArrayList<BlockStat> res = new ArrayList<BlockStat>();

    for (BlockStatEntity stat : userStats) {
      res.add(new BlockStat(stat));
    }

    return res;
  };

  public static int[] updateBatchBlockStatsCounts(ArrayList<BlockStatEntity> blockStats) {
    return repo.updateBatchBlockStatsCounts(blockStats);
  }

  public static long saveBlockStat(BlockStatEntity blockStat) {
    return repo.saveBlockStat(blockStat);
  }

  public static void addDisplayNameColumn() {
    repo.addDisplayNameColumn();
  }

  public static void createActivityStatsTable() {
    repo.createActivityStatsTable();
  }

  public static ArrayList<ActivityStat> getUserActivityStats(Long userId) {
    final ArrayList<ActivityStatEntity> userStats = repo.getUserActivityStats(userId);

    ArrayList<ActivityStat> res = new ArrayList<ActivityStat>();

    for (ActivityStatEntity stat : userStats) {
      res.add(new ActivityStat(stat));
    }

    return res;
  };

  public static int[] updateBatchActivityStatsCounts(ArrayList<ActivityStatEntity> activityStats) {
    return repo.updateBatchActivityStatsCounts(activityStats);
  }

  public static long saveActivityStat(ActivityStatEntity blockStat) {
    return repo.saveActivityStat(blockStat);
  }
}