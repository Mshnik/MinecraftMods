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
  private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(this::createHandler);

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
    handler.ifPresent(h -> h.deserializeNBT(handlerCompoundNBT));
    super.read(compoundNBT);
  }

  @Override
  public CompoundNBT write(CompoundNBT compoundNBT) {
    handler
        .map(ItemStackHandler::serializeNBT)
        .ifPresent(handlerCompoundNBT -> compoundNBT.put(INVENTORY_KEY, handlerCompoundNBT));
    return super.write(compoundNBT);
  }

  private ItemStackHandler createHandler() {
    return new ItemStackHandler() {
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

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return handler.cast();
    } else {
      return super.getCapability(cap, side);
    }
  }
}
