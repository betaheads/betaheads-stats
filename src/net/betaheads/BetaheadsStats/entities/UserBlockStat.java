package net.betaheads.BetaheadsStats.entities;

import java.util.HashMap;

import org.bukkit.Material;

public class UserBlockStat {
  private final HashMap<Material, MaterialBlockStat> userStatsMap = new HashMap();

  public void setUserBlockStat(Material material, MaterialBlockStat materialBlockStat) {
    userStatsMap.put(material, materialBlockStat);
  }

  public MaterialBlockStat getUserBlockStat(Material material) {
    return userStatsMap.get(material);
  }
}
