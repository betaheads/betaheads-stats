package net.betaheads.BetaheadsStats.entities;

import org.bukkit.Material;

import net.betaheads.BetaheadsStats.entities.enums.BlockAction;
import net.betaheads.utils.db.entities.BlockStatEntity;

public class BlockStat extends BlockStatEntity {

  BlockStat(int userId, Material block, BlockAction action) {
    this.loadData();
  }

  public void increaseCount() {
    this.count++;
  }

  public void updateDbData() {
  }

  public void createDbData() {
  }

  private void loadData() {
  }
}
