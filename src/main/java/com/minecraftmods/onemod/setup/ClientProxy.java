package com.minecraftmods.onemod.setup;

import com.minecraftmods.onemod.blocks.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/** @author Mshnik */
public final class ClientProxy implements IProxy {
  @Override
  public void init() {
    ModBlocks.registerScreenManagers();
  }

  @Override
  public World getClientWorld() {
    return Minecraft.getInstance().world;
  }

  @Override
  public PlayerEntity getClientPlayer() {
    return Minecraft.getInstance().player;
  }
}
