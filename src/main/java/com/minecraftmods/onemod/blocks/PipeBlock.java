package com.minecraftmods.onemod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/** @author Mshnik */
final class PipeBlock extends Block {
  private static final Properties PROPERTIES =
      Properties.create(Material.IRON)
          .sound(SoundType.METAL)
          .hardnessAndResistance(2.0f)
          .lightValue(14);

  PipeBlock() {
    super(PROPERTIES);
  }

  @Override
  public boolean hasTileEntity(BlockState blockState) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader world) {
    return new PipeBlockTile();
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



  @Override
  public boolean onBlockActivated(
      BlockState state,
      World worldIn,
      BlockPos pos,
      PlayerEntity player,
      Hand handIn,
      BlockRayTraceResult hit) {
    if (!worldIn.isRemote) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof INamedContainerProvider) {
        // Player cast is safe because world is not remote.
        // Tile Entity cast is safe from earlier instanceof.
        NetworkHooks.openGui(
            (ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
      }
    }
    return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
  }
}
