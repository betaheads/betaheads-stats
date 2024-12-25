package net.betaheads.utils.db;

import java.util.ArrayList;

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
}