package com.minecraftmods.onemod.setup;

import net.minecraft.world.World;

/** @author Mshnik */
public interface IProxy {

  void init();

  World getClientWorld();
}
