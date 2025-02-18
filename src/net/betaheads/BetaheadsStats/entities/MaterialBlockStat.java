package net.betaheads.BetaheadsStats.entities;

import java.util.HashMap;

import net.betaheads.BetaheadsStats.entities.enums.BlockAction;

public class MaterialBlockStat {
  private final HashMap<BlockAction, BlockStat> blockStatsMap = new HashMap<>();

  public void setBlockStat(BlockAction action, BlockStat stat) {
    blockStatsMap.put(action, stat);
  }

  public BlockStat geBlockStat(BlockAction action) {
    return blockStatsMap.get(action);
  }
}
