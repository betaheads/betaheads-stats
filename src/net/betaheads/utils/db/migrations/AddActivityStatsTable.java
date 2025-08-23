package net.betaheads.utils.db.migrations;

import net.betaheads.utils.db.Migration;
import net.betaheads.utils.db.Repository;

public class AddActivityStatsTable implements Migration {
  @Override
  public String getName() {
    return "AddActivityStatsTable";
  }

  @Override
  public void run() {
    Repository.createActivityStatsTable();
  }
}
