package net.betaheads.utils.db.entities;

public class ActivityStatEntity {
  public volatile long id; // 0 means not saved to DB yet
  public long user_id;
  public String activity;
  public String type;
  public volatile long count;
}
