package net.betaheads.BetaheadsStats;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.betaheads.BetaheadsStats.entities.ActivityStat;
import net.betaheads.BetaheadsStats.entities.enums.Activity;
import net.betaheads.BetaheadsStats.entities.enums.ActivityType;
import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.entities.ActivityStatEntity;

public class ActivityStatsManager {
  private final static ConcurrentHashMap<Long, ConcurrentHashMap<String, ActivityStat>> activityStatsMap = new ConcurrentHashMap<>();

  public static void addUserRecords(Long userId) {
    final ArrayList<ActivityStat> userActivityStats = Repository.getUserActivityStats(userId);
    ConcurrentHashMap<String, ActivityStat> userActivityStatsMap = new ConcurrentHashMap<>();

    for (ActivityStat activityStatStat : userActivityStats) {
      String key = buildMapKey(activityStatStat.type, activityStatStat.activity);

      userActivityStatsMap.put(key, activityStatStat);
    }

    activityStatsMap.put(userId, userActivityStatsMap);
  }

  public static ConcurrentHashMap<String, ActivityStat> getUserActivityStats(long userId) {
    return activityStatsMap.get(userId);
  }

  public static void handleUserActivity(long userId, Activity activity, ActivityType type) {
    handleUserActivity(userId, activity, type, 1);
  }

  public static void handleUserActivity(long userId, Activity activity, ActivityType type, long amount) {
    ConcurrentHashMap<String, ActivityStat> userStat = activityStatsMap.get(userId);

    if (userStat == null) { // user already quit
      return;
    }

    String activityStatKey = buildMapKey(type.toString(), activity.toString());

    ActivityStat activityStat = userStat.get(activityStatKey);

    if (activityStat == null) {
      activityStat = new ActivityStat();

      activityStat.user_id = userId;
      activityStat.type = type.toString();
      activityStat.activity = activity.toString();
      activityStat.count = amount;

      Long id = activityStat.saveToDb();

      activityStat.id = id;

      userStat.put(activityStatKey, activityStat);
    } else {
      activityStat.increaseCount(amount);
    }
  }

  public static void removeUserRecords(long userId) {
    ArrayList<ActivityStatEntity> stats = new ArrayList<>();

    for (ActivityStat activityStat : activityStatsMap.get(userId).values()) {
      stats.add(activityStat);
    }

    Repository.updateBatchActivityStatsCounts(stats);

    activityStatsMap.remove(userId);
  }

  public static void saveAllCounts() {
    ArrayList<ActivityStatEntity> stats = new ArrayList<>();

    for (ConcurrentHashMap<String, ActivityStat> userStats : activityStatsMap.values()) {
      for (ActivityStat stat : userStats.values()) {
        stats.add(stat);
      }
    }

    Repository.updateBatchActivityStatsCounts(stats);
  };

  public static String buildMapKey(String type, String activity) {
    return type + ":" + activity;
  }
}
