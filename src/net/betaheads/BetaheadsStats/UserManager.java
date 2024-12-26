package net.betaheads.BetaheadsStats;

import java.util.ArrayList;
import java.util.HashMap;

import net.betaheads.BetaheadsStats.entities.User;
import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.entities.UserEntity;

public class UserManager {
  private static HashMap<String, User> users = new HashMap<String, User>();

  public static void addUser(String username) {
    User user = new User(username);

    users.put(username, user);
  }

  public static void removeUser(String username) {
    User user = users.get(username);

    user.updateDbData();

    users.remove(username);
  }

  public static User getUser(String username) {
    return users.get(username);
  }

  public static void saveAllUsersData() {
    ArrayList<UserEntity> userEntities = new ArrayList<>();

    for (User user : users.values()) {
      user.played_ms = user.getTotalPlayedTime();

      userEntities.add(user);
    }

    Repository.updateUserBatch(userEntities);
  }
}
