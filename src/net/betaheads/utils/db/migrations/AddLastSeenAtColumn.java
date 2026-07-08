package net.betaheads.utils.db.migrations;

import net.betaheads.utils.db.Migration;
import net.betaheads.utils.db.Repository;

public class AddLastSeenAtColumn implements Migration {
  @Override
  public String getName() {
    return "AddLastSeenAtColumn";
  }

  @Override
  public void run() {
    Repository.addLastSeenAtColumn();
  }
}
