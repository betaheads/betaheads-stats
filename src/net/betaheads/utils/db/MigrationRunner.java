package net.betaheads.utils.db;

import java.util.ArrayList;

import net.betaheads.utils.PluginLogger;
import net.betaheads.utils.db.migrations.AddUserTable;

public class MigrationRunner {
  private static Migration[] migrations = {
      new AddUserTable()
  };

  public static void runMigrations() {
    createMigrationsTable();

    ArrayList<String> writtenMigrations = getAllWrittenMigrations();

    for (Migration migration : migrations) {
      if (!writtenMigrations.contains(migration.getName())) {
        migration.run();

        writeMigration(migration.getName());

        PluginLogger.info("[runMigrations]: migration - " + migration.getName() + " - written into DB.");
      }
    }
  }

  private static void createMigrationsTable() {
    Database.execute(
        "CREATE TABLE IF NOT EXISTS migrations (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    name VARCHAR(100)," +
            "    CONSTRAINT UQ_name UNIQUE (name)" +
            ");");
  }

  private static ArrayList<String> getAllWrittenMigrations() {
    ArrayList<ArrayList<Object>> res = Database.executeQuery("SELECT name FROM migrations;", true, 1);

    ArrayList<String> migrationNames = new ArrayList<String>();

    for (ArrayList<Object> migration : res) {
      migrationNames.add(migration.get(0).toString());
    }

    return migrationNames;
  }

  private static void writeMigration(String migrationName) {
    Database.executeUpdate("INSERT INTO migrations(name) VALUES('" + migrationName + "');");
  }
}
