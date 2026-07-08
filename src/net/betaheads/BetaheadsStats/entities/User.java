package net.betaheads.BetaheadsStats.entities;

import java.sql.Timestamp;

import net.betaheads.utils.PluginLogger;
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
    this.last_seen_at = new Timestamp(System.currentTimeMillis());

    Repository.updateUser(this);
  }

  private void loadUserData() {
    UserEntity user = Repository.getUser(this.name);

    if (user == null) {
      this.played_ms = 0;
      this.first_login_at = new Timestamp(this.joinTimeMs);
      this.last_login_at = new Timestamp(this.joinTimeMs);
      this.last_seen_at = new Timestamp(this.joinTimeMs);
      this.login_count = 1;

      Repository.saveUser(this);

      user = Repository.getUser(this.name);

      if (user == null) { // DB is unreachable or broken, keep defaults to avoid NPEs
        PluginLogger.error("[User] failed to load user data for '" + this.name + "', check DB errors above.");

        return;
      }
    } else {
      user.last_login_at = new Timestamp(this.joinTimeMs);
      user.login_count = user.login_count + 1;

      Repository.updateUserLogin(user);
    }

    this.id = user.id;
    this.name = user.name;
    this.played_ms = user.played_ms;
    this.first_login_at = user.first_login_at;
    this.last_login_at = user.last_login_at;
    this.last_seen_at = user.last_seen_at;
    this.login_count = user.login_count;

    this.totalWhenJoin = this.played_ms;
  }
}
