package com.minecraftmods.onemod.blocks;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraftforge.registries.ObjectHolder;

/** @author Mshnik */
public final class ModBlocks {
  private static ImmutableList<Class<? extends Block>> BLOCK_CLASSES;
  private static boolean initted = false;

  @ObjectHolder("onemod:firstblock")
  public static FirstBlock FIRST_BLOCK;

  /** Initializes the ModBlocks class. Only needs to be called once. Further calls no-op. */
  public static synchronized void init() {
    if (initted) {
      return;
    }

    BLOCK_CLASSES = ImmutableList.of(FirstBlock.class);
    initted = true;
  }

  /**
   * Returns a list of instances of each mod block. Blocks are instantiated in the same order they
   * are present in {@link #BLOCK_CLASSES}.
   */
  public static ImmutableList<Block> getModBlocks() {
    return BLOCK_CLASSES
        .stream()
        .map(ModBlocks::constructBlock)
        .collect(ImmutableList.toImmutableList());
  }

  /** Builds a {@link Block} from the given {@code blockClass}. Exceptions are re-thrown. */
  private static Block constructBlock(Class<? extends Block> blockClass) {
    try {
      return blockClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Error instantiating " + blockClass, e);
    }
  }
}
