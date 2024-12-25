package net.betaheads.BetaheadsStats.entities;

public class User {
  // database fields
  private long id;
  private String username;
  private long played_ms;

  // operational private fields
  private long joinTimeMs;

  User() {

  }

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
    return this.played_ms + getCurrentSessionPlayTime();
  }

  public long getCurrentSessionPlayTime() {
    return System.currentTimeMillis() - (System.currentTimeMillis() - joinTimeMs);
  }

  public void saveDataToDb() {
    this.played_ms = getTotalPlayedTime();

    // Database.executeUpdate(
    //     "INSERT INTO players (username, played_ms) " +
    //         "VALUES ('" + this.username + "'," + this.played_ms + ") " +
    //         "ON DUPLICATE KEY UPDATE played_ms = " + this.played_ms + ";");
  }
}
