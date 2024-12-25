package net.betaheads.BetaheadsStats.entities;

import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.entities.UserEntity;

public class User extends UserEntity {
  private long joinTimeMs;

  public User(String username) {
    this.name = username;

    this.joinTimeMs = System.currentTimeMillis();

    loadUserData();
  }

  public String getUsername() {
    return this.name;
  }

  public long getTotalPlayedTime() {
    return this.played_ms + getCurrentSessionPlayTime();
  }

  public long getCurrentSessionPlayTime() {
    return System.currentTimeMillis() - this.joinTimeMs;
  }

  public void saveDataToDb() {
    this.played_ms = getTotalPlayedTime();

    Repository.saveUser(this);
  }

  public void loadUserData() {
    UserEntity user = Repository.getUser(this.name);

    if (user == null) {
      this.played_ms = 0;
      this.saveDataToDb();

      user = Repository.getUser(this.name);
    }

    this.id = user.id;
    this.name = user.name;
    this.played_ms = user.played_ms;
  }
}
