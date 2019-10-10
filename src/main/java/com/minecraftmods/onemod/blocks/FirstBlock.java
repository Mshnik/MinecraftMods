package com.minecraftmods.onemod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/** @author Mshnik */
final class FirstBlock extends Block {
  private static final Properties PROPERTIES =
      Properties.create(Material.IRON)
          .sound(SoundType.METAL)
          .hardnessAndResistance(2.0f)
          .lightValue(14);

  FirstBlock() {
    super(PROPERTIES);
  }
}
