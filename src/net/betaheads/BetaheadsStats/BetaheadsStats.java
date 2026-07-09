package net.betaheads.BetaheadsStats;

import org.bukkit.Bukkit;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.betaheads.BetaheadsStats.commands.StatsCommand;
import net.betaheads.BetaheadsStats.listeners.BhBlockListener;
import net.betaheads.BetaheadsStats.listeners.BhEntityListener;
import net.betaheads.BetaheadsStats.listeners.BhPlayerListener;
import net.betaheads.BetaheadsStats.tasks.SaveActivityStats;
import net.betaheads.BetaheadsStats.tasks.SaveBlockStats;
import net.betaheads.BetaheadsStats.tasks.SaveUsers;
import net.betaheads.BetaheadsStats.tasks.TrackPlayersMovement;
import net.betaheads.utils.PluginLogger;
import net.betaheads.utils.db.MigrationRunner;
import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.datasources.MySqlDatasource;

public class BetaheadsStats extends JavaPlugin {
  public static BetaheadsStats plugin = null;

  @Override
  public void onLoad() {
    Config.loadConfigFile();

    PluginLogger.setLogger(getServer().getLogger());

    Repository.setRepository(new MySqlDatasource()); // only mysql for now
    Repository.init();

    MigrationRunner.runMigrations();
  }

  @Override
  public void onEnable() {
    plugin = this;
    PluginManager pm = Bukkit.getServer().getPluginManager();

    getServer().getScheduler().scheduleAsyncRepeatingTask(this, new SaveUsers(), 6000L, 6000L); // every 5 mins
    getServer().getScheduler().scheduleAsyncRepeatingTask(this, new SaveBlockStats(), 6000L, 6000L); // every 5 mins
    getServer().getScheduler().scheduleAsyncRepeatingTask(this, new SaveActivityStats(), 6000L, 6000L); // every 5 mins
    getServer().getScheduler().scheduleSyncRepeatingTask(this, new TrackPlayersMovement(),
        TrackPlayersMovement.SAMPLE_PERIOD_TICKS, TrackPlayersMovement.SAMPLE_PERIOD_TICKS); // every 2 secs, sync to read locations

    BhPlayerListener playerListener = new BhPlayerListener();
    BhBlockListener blockListener = new BhBlockListener();
    BhEntityListener entityListener = new BhEntityListener();

    pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_FISH, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_DROP_ITEM, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_BUCKET_FILL, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_BUCKET_EMPTY, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_BED_ENTER, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_PORTAL, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_EGG_THROW, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Lowest, this);
    pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Lowest, this);
    pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Lowest, this);
    pm.registerEvent(Type.BLOCK_IGNITE, blockListener, Priority.Lowest, this);
    pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Lowest, this);
    pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Lowest, this);
    pm.registerEvent(Type.PAINTING_PLACE, entityListener, Priority.Lowest, this);

    this.getCommand("stats").setExecutor(new StatsCommand());

    PluginLogger.info("Enabled.");
  }

  @Override
  public void onDisable() {
    UserManager.saveAllUsersData();
    BlockStatsManager.saveAllCounts();
    ActivityStatsManager.saveAllCounts();
  }
}
