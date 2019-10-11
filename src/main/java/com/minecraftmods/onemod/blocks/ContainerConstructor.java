package com.minecraftmods.onemod.blocks;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.function.Supplier;

/** @author Mshnik */
final class ContainerConstructor<C extends Container, S extends ContainerScreen<C>> {
  @FunctionalInterface
  interface CBuilder<C extends Container> {
    C apply(int id, World world, BlockPos blockPos, PlayerInventory playerInventory);
  }

  /** Creates a {@link ContainerConstructor} fom the given args. */
  static <C extends Container, S extends ContainerScreen<C>> ContainerConstructor<C, S> of(
      CBuilder<C> cBuilder,
      Supplier<ContainerType<C>> cTypeObjectHolderSupplier,
      ScreenManager.IScreenFactory<C, S> sBuilder) {
    return new ContainerConstructor<>(cBuilder, cTypeObjectHolderSupplier, sBuilder);
  }

  private final CBuilder<C> cBuilder;
  private final Supplier<ContainerType<C>> cTypeObjectHolderSupplier;
  private final ScreenManager.IScreenFactory<C, S> sBuilder;

  private ContainerConstructor(
      CBuilder<C> cBuilder,
      Supplier<ContainerType<C>> cTypeObjectHolderSupplier,
      ScreenManager.IScreenFactory<C, S> sBuilder) {
    this.cBuilder = cBuilder;
    this.cTypeObjectHolderSupplier = cTypeObjectHolderSupplier;
    this.sBuilder = sBuilder;
  }

  /** Builds a {@code C} from the given args. */
  C constructContainer(int id, World world, BlockPos blockPos, PlayerInventory playerInventory) {
    return cBuilder.apply(id, world, blockPos, playerInventory);
  }

  /** Returns the {@link ContainerType} in {@link #cTypeObjectHolderSupplier}. */
  ContainerType<C> getContainerType() {
    return cTypeObjectHolderSupplier.get();
  }

  /** Builds a {@code S} from the given args. */
  S constructScreen(C screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    return sBuilder.create(screenContainer, inv, titleIn);
  }
}
