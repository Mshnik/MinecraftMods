package com.minecraftmods.onemod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

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

  @Override
  public void onBlockPlacedBy(
      World world,
      BlockPos blockPos,
      BlockState blockState,
      @Nullable LivingEntity entity,
      ItemStack stack) {
    if (entity != null) {
      world.setBlockState(
          blockPos,
          blockState.with(BlockStateProperties.FACING, getFacingFromEntity(blockPos, entity)),
          2);
    }
  }

  private static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
    return Direction.getFacingFromVector(
        (float) (entity.posX - clickedBlock.getX()),
        (float) (entity.posY - clickedBlock.getY()),
        (float) (entity.posZ - clickedBlock.getZ()));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(BlockStateProperties.FACING);
  }
}
