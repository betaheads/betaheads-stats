package net.betaheads.BetaheadsStats;

import java.util.HashMap;

import net.betaheads.BetaheadsStats.entities.User;

public class UserManager {
  private HashMap<String, User> users = new HashMap<String, User>();

  public void addUser(String username) {
    User user = new User(username);

    users.put(username, user);
  }

  public void removeUser(String username) {
    User user = users.get(username);

    user.saveDataToDb();

    users.remove(username);
  }

  public User getUser(String username) {
    return users.get(username);
  }
}
