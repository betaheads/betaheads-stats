package net.betaheads.BetaheadsStats;

import java.util.ArrayList;
import java.util.HashMap;

import net.betaheads.BetaheadsStats.entities.User;
import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.entities.UserEntity;

public class UserManager {
  private static HashMap<String, User> usersMap = new HashMap<String, User>();

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

    for (User user : usersMap.values()) {
      user.played_ms = user.getTotalPlayedTime();

      userEntities.add(user);
    }

    Repository.updateUserBatch(userEntities);
  }
}
