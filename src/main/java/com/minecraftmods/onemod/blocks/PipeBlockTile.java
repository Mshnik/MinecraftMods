package com.minecraftmods.onemod.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** @author Mshnik */
final class PipeBlockTile extends TileEntity
    implements ITickableTileEntity, INamedContainerProvider {
  private static final String INVENTORY_KEY = "inventory";
  private static final int NUM_STACKS = 2;

  private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(this::createHandler);

  PipeBlockTile() {
    super(ObjectHolders.PIPE_BLOCK_TILE);
  }

  @Override
  public void tick() {
    //    if (world.isRemote) {
    //      System.out.println("PipeBlockTile.tick");
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

  /** Returns true iff the item handler has at least one item. */
  boolean hasItem() {
    return handler.map(i -> i.getStackInSlot(0).getItem() != Items.AIR).orElse(false);
  }

  private ItemStackHandler createHandler() {
    return new ItemStackHandler(NUM_STACKS);
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

  @Override
  public ITextComponent getDisplayName() {
    return new StringTextComponent(getType().getRegistryName().getPath());
  }

  @Override
  public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new PipeBlockContainer(id, world, pos, playerInventory);
  }
}
