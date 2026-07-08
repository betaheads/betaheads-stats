package net.betaheads.BetaheadsStats.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.betaheads.BetaheadsStats.ActivityStatsManager;
import net.betaheads.BetaheadsStats.BetaheadsStats;
import net.betaheads.BetaheadsStats.UserManager;
import net.betaheads.BetaheadsStats.entities.User;
import net.betaheads.BetaheadsStats.entities.enums.Activity;
import net.betaheads.BetaheadsStats.entities.enums.ActivityType;

// runs SYNC on the main thread to safely read player locations,
// then hands the accumulated increments off to an async task
public class TrackPlayersMovement implements Runnable {
  public static final long SAMPLE_PERIOD_TICKS = 40L; // 2 seconds
  private static final long SAMPLE_PERIOD_SECONDS = SAMPLE_PERIOD_TICKS / 20;
  private static final double TELEPORT_DISTANCE_THRESHOLD = 100; // blocks per sample, anything above is a teleport

  private final ConcurrentHashMap<String, Sample> lastSamples = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Double> metersRemainders = new ConcurrentHashMap<>();

  @Override
  public void run() {
    Player[] players = Bukkit.getServer().getOnlinePlayers();

    ArrayList<Increment> increments = new ArrayList<>();
    HashSet<String> onlineNames = new HashSet<>();

    for (Player player : players) {
      String username = player.getName();

      onlineNames.add(username);

      Location location = player.getLocation();
      World world = location.getWorld();

      if (world.getEnvironment() == World.Environment.NETHER) {
        increments.add(new Increment(username, Activity.TIME_IN_NETHER, SAMPLE_PERIOD_SECONDS));
      }

      Sample previous = lastSamples.get(username);
      Sample current = new Sample(world.getName(), location.getX(), location.getY(), location.getZ(),
          player.isInsideVehicle());

      lastSamples.put(username, current);

      if (previous == null || !previous.world.equals(current.world)) {
        continue;
      }

      double dx = current.x - previous.x;
      double dy = current.y - previous.y;
      double dz = current.z - previous.z;

      double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

      if (distance <= 0 || distance > TELEPORT_DISTANCE_THRESHOLD) {
        continue;
      }

      Activity activity = current.inVehicle ? Activity.DISTANCE_BY_VEHICLE : Activity.DISTANCE_WALKED;

      String remainderKey = username + ":" + activity;

      Double remainder = metersRemainders.get(remainderKey);
      double total = (remainder == null ? 0 : remainder) + distance;

      long wholeMeters = (long) total;

      metersRemainders.put(remainderKey, total - wholeMeters);

      if (wholeMeters > 0) {
        increments.add(new Increment(username, activity, wholeMeters));
      }
    }

    cleanupOfflinePlayers(onlineNames);

    if (increments.isEmpty()) {
      return;
    }

    Bukkit.getScheduler().scheduleAsyncDelayedTask(BetaheadsStats.plugin, () -> {
      for (Increment increment : increments) {
        User user = UserManager.getUser(increment.username);

        if (user == null) { // user already quit
          continue;
        }

        ActivityStatsManager.handleUserActivity(user.id, increment.activity, ActivityType.COMMON, increment.amount);
      }
    });
  }

  private void cleanupOfflinePlayers(HashSet<String> onlineNames) {
    for (String username : lastSamples.keySet()) {
      if (!onlineNames.contains(username)) {
        lastSamples.remove(username);
      }
    }

    for (String remainderKey : metersRemainders.keySet()) {
      String username = remainderKey.substring(0, remainderKey.indexOf(":"));

      if (!onlineNames.contains(username)) {
        metersRemainders.remove(remainderKey);
      }
    }
  }

  private static class Sample {
    public final String world;
    public final double x;
    public final double y;
    public final double z;
    public final boolean inVehicle;

    public Sample(String world, double x, double y, double z, boolean inVehicle) {
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
      this.inVehicle = inVehicle;
    }
  }

  private static class Increment {
    public final String username;
    public final Activity activity;
    public final long amount;

    public Increment(String username, Activity activity, long amount) {
      this.username = username;
      this.activity = activity;
      this.amount = amount;
    }
  }
}
