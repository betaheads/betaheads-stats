package net.betaheads.BetaheadsStats.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BhBlockListener extends BlockListener {
  @Override
  public void onBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    Material type = block.getType();

    event.getPlayer().sendMessage(type.toString());
  }

  public void onBlockPlace(BlockPlaceEvent event) {
    Block block = event.getBlock();
    Material type = block.getType();

    event.getPlayer().sendMessage(type.toString());
  }
}
