package net.betaheads.BetaheadsStats.entities;

import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.entities.UserEntity;

public class User extends UserEntity {
  private long joinTimeMs;
  private long totalWhenJoin;

  public User(String username, String displayName) {
    this.name = username;
    this.display_name = displayName;

    this.joinTimeMs = System.currentTimeMillis();

    loadUserData();
  }

  public String getUsername() {
    return this.name;
  }

  public long getTotalPlayedTime() {
    return this.totalWhenJoin + getCurrentSessionPlayTime();
  }

  public long getCurrentSessionPlayTime() {
    return System.currentTimeMillis() - this.joinTimeMs;
  }

  public void updateDbData() {
    this.played_ms = getTotalPlayedTime();

    Repository.updateUser(this);
  }

  private void loadUserData() {
    UserEntity user = Repository.getUser(this.name);

    if (user == null) {
      this.played_ms = 0;
      Repository.saveUser(this);

      user = Repository.getUser(this.name);
    }

    this.id = user.id;
    this.name = user.name;
    this.played_ms = user.played_ms;

    this.totalWhenJoin = this.played_ms;
  }
}
