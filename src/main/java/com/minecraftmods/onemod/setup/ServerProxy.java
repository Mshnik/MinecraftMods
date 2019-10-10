package com.minecraftmods.onemod.setup;

import net.minecraft.world.World;

/** @author Mshnik */
public final class ServerProxy implements IProxy {
  @Override
  public void init() {}

  @Override
  public World getClientWorld() {
    throw new IllegalStateException("Only run this on the client.");
  }
}
