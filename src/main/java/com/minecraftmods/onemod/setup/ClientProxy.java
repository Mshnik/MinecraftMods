package com.minecraftmods.onemod.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/** @author Mshnik */
public final class ClientProxy implements IProxy {
  @Override
  public World getClientWorld() {
    return Minecraft.getInstance().world;
  }
}
