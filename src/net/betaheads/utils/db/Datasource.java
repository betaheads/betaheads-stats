package net.betaheads.utils.db;

import java.util.ArrayList;

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
}
