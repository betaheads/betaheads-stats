package net.betaheads.BetaheadsStats.tasks;

import net.betaheads.BetaheadsStats.BlockStatsManager;

public class SaveBlockStats implements Runnable {
  public void run() {
    BlockStatsManager.saveAllCounts();
  }
}
