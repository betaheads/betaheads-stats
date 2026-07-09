package net.betaheads.utils.db.entities;

public class BlockStatEntity {
  public volatile long id; // 0 means not saved to DB yet
  public long user_id;
  public String block;
  public String action;
  public volatile long count;
}
