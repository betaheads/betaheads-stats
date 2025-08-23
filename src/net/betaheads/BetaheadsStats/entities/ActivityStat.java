package net.betaheads.BetaheadsStats.entities;

import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.entities.ActivityStatEntity;

public class ActivityStat extends ActivityStatEntity {
  public ActivityStat() {
  };

  public ActivityStat(ActivityStatEntity entity) {
    this.id = entity.id;
    this.user_id = entity.user_id;
    this.activity = entity.activity;
    this.type = entity.type;
    this.count = entity.count;
  }

  public void increaseCount() {
    this.count++;
  }

  public long saveToDb() {
    return Repository.saveActivityStat(this);
  }
}
