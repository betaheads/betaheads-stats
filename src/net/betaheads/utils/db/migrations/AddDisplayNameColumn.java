package net.betaheads.utils.db.migrations;

import net.betaheads.utils.db.Migration;
import net.betaheads.utils.db.Repository;

public class AddDisplayNameColumn implements Migration {
  @Override
  public void run() {
    Repository.addDisplayNameColumn();
  }

  @Override
  public String getName() {
    return "AddDisplayNameColumn";
  }
}
