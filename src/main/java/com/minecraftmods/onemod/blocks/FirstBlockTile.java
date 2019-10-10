package com.minecraftmods.onemod.blocks;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

/** @author Mshnik */
final class FirstBlockTile extends TileEntity implements ITickableTileEntity {
  FirstBlockTile() {
    super(ObjectHolders.FIRST_BLOCK_TILE);
  }

  @Override
  public void tick() {
    if (world.isRemote) {
      System.out.println("FirstBlockTile.tick");
    }
  }
}
