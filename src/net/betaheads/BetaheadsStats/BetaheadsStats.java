package net.betaheads.BetaheadsStats;

import org.bukkit.Bukkit;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.betaheads.BetaheadsStats.commands.StatsCommand;
import net.betaheads.BetaheadsStats.listeners.BhBlockListener;
import net.betaheads.BetaheadsStats.listeners.BhPlayerListener;
import net.betaheads.BetaheadsStats.tasks.SaveActivityStats;
import net.betaheads.BetaheadsStats.tasks.SaveBlockStats;
import net.betaheads.BetaheadsStats.tasks.SaveUsers;
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

    BhPlayerListener playerListener = new BhPlayerListener();
    BhBlockListener blockListener = new BhBlockListener();

    pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Lowest, this);
    pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Lowest, this);
    pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Lowest, this);

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
