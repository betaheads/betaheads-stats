package net.betaheads.BetaheadsStats;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.betaheads.BetaheadsStats.entities.User;
import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.entities.UserEntity;

public class UserManager {
  private static ConcurrentHashMap<String, User> usersMap = new ConcurrentHashMap<String, User>();

  public static void addUser(String displayName) {
    String username = displayName.toLowerCase();

    User user = new User(username, displayName);

    usersMap.put(username, user);
  }

  public static void removeUser(String displayName) {
    String username = displayName.toLowerCase();

    User user = usersMap.get(username);

    user.updateDbData();

    usersMap.remove(username);
  }

  public static User getUser(String displayName) {
    String username = displayName.toLowerCase();

    return usersMap.get(username);
  }

  public static void saveAllUsersData() {
    ArrayList<UserEntity> userEntities = new ArrayList<>();

    Timestamp now = new Timestamp(System.currentTimeMillis());

    for (User user : usersMap.values()) {
      user.played_ms = user.getTotalPlayedTime();
      user.last_seen_at = now; // user is online right now

      userEntities.add(user);
    }

    Repository.updateUserBatch(userEntities);
  }
}
