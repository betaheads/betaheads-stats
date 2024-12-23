package net.betaheads.utils.db.migrations;

import net.betaheads.utils.db.Database;
import net.betaheads.utils.db.Migration;

public class AddUserTable implements Migration {
  @Override
  public String getName() {
    return "AddUserTable";
  }

  @Override
  public void run() {
    Database.execute(
        "CREATE TABLE users (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "name VARCHAR(255) UNIQUE," +
            "played_ms BIGINT" +
            ");");
  }
}
