package net.betaheads.BetaheadsStats.entities;

import net.betaheads.utils.Database;

public class Player {
  // database fields
  private long id;
  private String username;
  private long played_ms;

  // operational private fields
  private long joinTimeMs;

  public long getId() {
    return this.id;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setJoinTimeMs(long joinTimeMs) {
    this.joinTimeMs = joinTimeMs;
  }

  public long getTotalPlayedTime() {
    return played_ms + getCurrentSessionPlayTime();
  }

  public long getCurrentSessionPlayTime() {
    return System.currentTimeMillis() - (System.currentTimeMillis() - joinTimeMs);
  }

  public void saveDataToDb() {
    Database.executeQuery(
        "INSERT INTO players (username, played_ms) " +
            "VALUES ('" + this.username + "'," + this.played_ms + ") " +
            "ON DUPLICATE KEY UPDATE played_ms = " + this.played_ms + ";",
        false, 0);
  }
}
