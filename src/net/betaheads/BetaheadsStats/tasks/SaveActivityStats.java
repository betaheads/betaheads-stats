package net.betaheads.BetaheadsStats.tasks;

import net.betaheads.BetaheadsStats.ActivityStatsManager;

public class SaveActivityStats implements Runnable {
  public void run() {
    ActivityStatsManager.saveAllCounts();
  }
}
