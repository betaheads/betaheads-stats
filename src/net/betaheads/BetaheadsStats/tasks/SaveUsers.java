package net.betaheads.BetaheadsStats.tasks;

import net.betaheads.BetaheadsStats.UserManager;

public class SaveUsers implements Runnable {
  @Override
  public void run() {
    UserManager.saveAllUsersData();
  }
}
