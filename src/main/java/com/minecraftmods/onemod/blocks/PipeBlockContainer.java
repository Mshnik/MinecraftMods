package com.minecraftmods.onemod.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

/** @author Mshnik */
final class PipeBlockContainer extends Container {
  private static final int GUI_GRID_SIZE_PX = 18;

  private final TileEntity tileEntity;
  private final IItemHandler playerInventoryHandler;

  PipeBlockContainer(int id, World world, BlockPos pos, PlayerInventory playerInventory) {
    super(ObjectHolders.PIPE_BLOCK_CONTAINER, id);
    tileEntity = world.getTileEntity(pos);
    playerInventoryHandler = new InvWrapper(playerInventory);

    // Block inventory.
    tileEntity
        .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        .ifPresent(h -> addSlots(h, 79, 29, 1, 1, 0));

    // Player inventory.
    layoutPlayerInventorySlots(8, 84);
  }

  TileEntity getTileEntity() {
    return tileEntity;
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return isWithinUsableDistance(
        IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()),
        playerIn,
        ObjectHolders.PIPE_BLOCK);
  }

  private void layoutPlayerInventorySlots(int leftCol, int topRow) {
    // Main inventory.
    addSlots(playerInventoryHandler, leftCol, topRow, 9, 3, 9);

    // Hotbar.
    topRow += 58;
    addSlots(playerInventoryHandler, leftCol, topRow, 9, 1, 0);
  }

  /**
   * Adds a grid of slots to this container.
   *
   * @param handler - the {@link IItemHandler} to attach to the slots handlers.
   * @param xStart - the top left x-coordinate to start at.
   * @param yStart - the top left y-coordinate to start at.
   * @param countX - number of cells horizontally in a single row.
   * @param countY - number of cells vertically in a single column.
   * @param initialIndex - the index number to start at.
   */
  private void addSlots(
      IItemHandler handler, int xStart, int yStart, int countX, int countY, int initialIndex) {
    int x = xStart;
    int y = yStart;
    int index = initialIndex;
    for (int r = 0; r < countY; r++) {
      for (int c = 0; c < countX; c++) {
        addSlot(new SlotItemHandler(handler, index, x, y));
        x += GUI_GRID_SIZE_PX;
        index++;
      }
      x = xStart;
      y += GUI_GRID_SIZE_PX;
    }
  }
}
