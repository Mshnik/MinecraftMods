package com.example.examplemod.setup;

import net.minecraft.world.World;

/** @author Mshnik */
public final class ServerProxy implements IProxy {
  @Override
  public World getClientWorld() {
    throw new IllegalStateException("Only run this on the client.");
  }
}
