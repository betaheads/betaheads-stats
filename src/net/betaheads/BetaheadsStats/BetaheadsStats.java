package net.betaheads.BetaheadsStats;

import org.bukkit.Bukkit;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.betaheads.BetaheadsStats.listeners.BhPlayerListener;
import net.betaheads.utils.PluginLogger;
import net.betaheads.utils.db.MigrationRunner;
import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.datasources.MySqlDatasource;

public class BetaheadsStats extends JavaPlugin {
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
    PluginManager pm = Bukkit.getServer().getPluginManager();

    BhPlayerListener playerListener = new BhPlayerListener();
    pm.registerEvent(Type.PLAYER_JOIN, playerListener,
        Priority.Lowest, this);
    pm.registerEvent(Type.PLAYER_QUIT, playerListener,
        Priority.Lowest, this);

  }

  @Override
  public void onDisable() {
    // TODO Auto-generated method stub
  }
}
