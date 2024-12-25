package net.betaheads.BetaheadsStats;

import org.bukkit.plugin.java.JavaPlugin;
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
    // TODO Auto-generated method stub
  }

  @Override
  public void onDisable() {
    // TODO Auto-generated method stub
  }
}
