package com.minecraftmods.onemod.blocks;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

/** @author Mshnik */
final class ObjectHolders {
  @ObjectHolder("onemod:firstblock")
  static FirstBlock FIRST_BLOCK;

  @ObjectHolder("onemod:firstblock")
  static TileEntityType<FirstBlockTile> FIRST_BLOCK_TILE;

  @ObjectHolder("onemod:firstblock")
  static ContainerType<FirstBlockContainer> FIRST_BLOCK_CONTAINER;

  @ObjectHolder("onemod:pipeblock")
  static PipeBlock PIPE_BLOCK;

  @ObjectHolder("onemod:pipeblock")
  static TileEntityType<PipeBlockTile> PIPE_BLOCK_TILE;

  @ObjectHolder("onemod:pipeblock")
  static ContainerType<PipeBlockContainer> PIPE_BLOCK_CONTAINER;

  private ObjectHolders() {}
}
