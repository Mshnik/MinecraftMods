package com.minecraftmods.onemod.blocks;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

/** @author Mshnik */
final class ObjectHolders {
  @ObjectHolder("onemod:firstblock")
  static FirstBlock FIRST_BLOCK;

  @ObjectHolder("onemod:firstblock")
  static TileEntityType<FirstBlockTile> FIRST_BLOCK_TILE;

  private ObjectHolders() {}
}
