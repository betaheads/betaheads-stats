package net.betaheads.utils.db.migrations;

import net.betaheads.utils.db.Migration;
import net.betaheads.utils.db.Repository;

public class AddUserLoginColumns implements Migration {
  @Override
  public String getName() {
    return "AddUserLoginColumns";
  }

  @Override
  public void run() {
    Repository.addUserLoginColumns();
  }
}
