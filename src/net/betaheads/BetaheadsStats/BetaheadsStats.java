package net.betaheads.BetaheadsStats;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import net.betaheads.utils.Database;

public class BetaheadsStats extends JavaPlugin {
  private Logger log;

  @Override
  public void onLoad() {
    Config.loadConfigFile();

    log = getServer().getLogger();

    Database.init(log);

    ArrayList<ArrayList<Object>> res = Database.executeQuery("SELECT * FROM untitled_table;", true, 3);

    log.info(res.get(0).get(2).toString());
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
