package net.betaheads.BetaheadsStats.entities;

import net.betaheads.utils.db.Repository;
import net.betaheads.utils.db.entities.BlockStatEntity;

public class BlockStat extends BlockStatEntity {

  public BlockStat() {
  };

  public BlockStat(BlockStatEntity entity) {
    this.id = entity.id;
    this.user_id = entity.user_id;
    this.block = entity.block;
    this.action = entity.action;
    this.count = entity.count;
  }

  public void increaseCount() {
    this.count++;
  }

  public long createDbData() {
    return Repository.saveBlockStat(this);
  }
}
