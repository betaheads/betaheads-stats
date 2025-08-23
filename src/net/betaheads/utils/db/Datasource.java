package net.betaheads.utils.db;

import java.util.ArrayList;

import net.betaheads.utils.db.entities.ActivityStatEntity;
import net.betaheads.utils.db.entities.BlockStatEntity;
import net.betaheads.utils.db.entities.UserEntity;

public interface Datasource {
  public enum DatasourceType {
    MySQL;
  }

  public void init();

  public DatasourceType getType();

  public ArrayList<String> getAllWrittenMigrations();

  public void writeMigration(String migrationName);

  public void createMigrationsTable();

  public void createUsersTable();

  public UserEntity getUser(String username);

  public int saveUser(UserEntity user);

  public int[] updateUserBatch(ArrayList<UserEntity> users);

  public int updateUser(UserEntity user);

  public void createBlockStatsTable();

  public ArrayList<BlockStatEntity> getUserBlockStats(Long userId);

  public int[] updateBatchBlockStatsCounts(ArrayList<BlockStatEntity> blockStats);

  public long saveBlockStat(BlockStatEntity blockStat);

  public void addDisplayNameColumn();

  public void createActivityStatsTable();

  public ArrayList<ActivityStatEntity> getUserActivityStats(Long userId);

  public int[] updateBatchActivityStatsCounts(ArrayList<ActivityStatEntity> activityStats);

  public long saveActivityStat(ActivityStatEntity activityStats);
}
