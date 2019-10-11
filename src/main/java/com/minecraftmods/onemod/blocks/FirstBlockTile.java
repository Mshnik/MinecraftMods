package com.minecraftmods.onemod.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** @author Mshnik */
final class FirstBlockTile extends TileEntity implements ITickableTileEntity {
  private static final String INVENTORY_KEY = "inventory";
  private ItemStackHandler handler;

  FirstBlockTile() {
    super(ObjectHolders.FIRST_BLOCK_TILE);
  }

  @Override
  public void tick() {
    //    if (world.isRemote) {
    //      System.out.println("FirstBlockTile.tick");
    //    }
  }

  @Override
  public void read(CompoundNBT compoundNBT) {
    CompoundNBT handlerCompoundNBT = compoundNBT.getCompound(INVENTORY_KEY);
    getHandler().deserializeNBT(handlerCompoundNBT);
    super.read(compoundNBT);
  }

  @Override
  public CompoundNBT write(CompoundNBT compoundNBT) {
    CompoundNBT handlerCompoundNBT = getHandler().serializeNBT();
    compoundNBT.put(INVENTORY_KEY, handlerCompoundNBT);
    return super.write(compoundNBT);
  }

  private ItemStackHandler getHandler() {
    if (handler == null) {
      handler =
          new ItemStackHandler() {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
              return stack.getItem() == Items.DIAMOND;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
              if (!isItemValid(slot, stack)) {
                return stack;
              }
              return super.insertItem(slot, stack, simulate);
            }
          };
    }
    return handler;
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      // T cast is safe here because ITEM_HANDLER_CAPABILITY is Capability<IItemHandler>.
      return LazyOptional.of(() -> (T) getHandler());
    }

    return super.getCapability(cap, side);
  }
}
