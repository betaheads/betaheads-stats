package net.betaheads.utils.db.entities;

import java.sql.Timestamp;

public class UserEntity {
  public long id;
  public String name;
  public String display_name;
  public long played_ms;
  public Timestamp first_login_at;
  public Timestamp last_login_at;
  public Timestamp last_seen_at;
  public long login_count;
}
