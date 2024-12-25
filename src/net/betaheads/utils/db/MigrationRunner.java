package net.betaheads.utils.db;

import java.util.ArrayList;

import net.betaheads.utils.PluginLogger;
import net.betaheads.utils.db.migrations.AddUserTable;

public class MigrationRunner {
  private static Migration[] migrations = {
      new AddUserTable()
  };

  public static void runMigrations() {
    Repository.createMigrationsTable();

    ArrayList<String> writtenMigrations = Repository.getAllWrittenMigrations();

    for (Migration migration : migrations) {
      if (!writtenMigrations.contains(migration.getName())) {
        migration.run();

        Repository.writeMigration(migration.getName());

        PluginLogger.info("[runMigrations]: migration - " + migration.getName() + " - written into DB.");
      }
    }
  }
}
