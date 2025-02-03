package net.betaheads.utils.db.migrations;

import net.betaheads.utils.db.Migration;
import net.betaheads.utils.db.Repository;

public class AddBlockStatsTable implements Migration{
  @Override
  public String getName() {
    return "AddBlockStatsTable";
  }

  @Override
  public void run() {
    Repository.createBlockStatsTable();
  }
}
