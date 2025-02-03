package net.betaheads.utils.db;

import java.util.ArrayList;

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

  public static ArrayList<BlockStatEntity> getUserBlockStats(String userId) {
    return repo.getUserBlockStats(userId);
  };

  public static int[] updateBatchBlockStatsCounts(ArrayList<BlockStatEntity> blockStats) {
    return repo.updateBatchBlockStatsCounts(blockStats);
  }

  public static int saveBlockStat(BlockStatEntity blockStat) {
    return repo.saveBlockStat(blockStat);
  }
}