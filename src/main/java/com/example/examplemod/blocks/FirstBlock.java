package com.example.examplemod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/** @author Mshnik */
public final class FirstBlock extends Block {
  public static final String REGISTRY_NAME = "firstblock";

  public FirstBlock() {
    super(
        Properties.create(Material.IRON)
            .sound(SoundType.METAL)
            .hardnessAndResistance(2.0f)
            .lightValue(14));
    setRegistryName(REGISTRY_NAME);
  }
}
