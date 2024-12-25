package net.betaheads.utils.db.migrations;

import net.betaheads.utils.db.Migration;
import net.betaheads.utils.db.Repository;

public class AddUserTable implements Migration {
  @Override
  public String getName() {
    return "AddUserTable";
  }

  @Override
  public void run() {
   Repository.createUsersTable();
  }
}
