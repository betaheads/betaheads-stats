package net.betaheads.BetaheadsStats;

import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;

import net.betaheads.utils.PluginLogger;
import net.betaheads.utils.db.Database;
import net.betaheads.utils.db.MigrationRunner;

public class BetaheadsStats extends JavaPlugin {
  @Override
  public void onLoad() {
    Config.loadConfigFile();

    PluginLogger.setLogger(getServer().getLogger());

    Database.init();

    MigrationRunner.runMigrations();

    ArrayList<ArrayList<Object>> res = Database.executeQuery("SELECT * FROM untitled_table;", true, 3);

    PluginLogger.info(res.get(0).get(2).toString());
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
