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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/** @author Mshnik */
final class PipeBlock extends Block {
  private static final Properties PROPERTIES =
      Properties.create(Material.IRON)
          .sound(SoundType.METAL)
          .hardnessAndResistance(0.5f)
          .lightValue(14);

  private static final float BLOCK_SIZE = 16;
  private static final float WIDTH = 6;
  private static final float SPACE = (BLOCK_SIZE - WIDTH) / 2;

  private static final VoxelShape STRAIGHT_SHAPE_EAST_WEST =
      Block.makeCuboidShape(0.0D, SPACE, SPACE, BLOCK_SIZE, WIDTH + SPACE, WIDTH + SPACE);
  private static final VoxelShape STRAIGHT_SHAPE_NORTH_SOUTH =
      Block.makeCuboidShape(SPACE, SPACE, 0.0D, WIDTH + SPACE, WIDTH + SPACE, BLOCK_SIZE);
  private static final VoxelShape STRAIGHT_SHAPE_UP_DOWN =
      Block.makeCuboidShape(SPACE, 0.0D, SPACE, WIDTH + SPACE, BLOCK_SIZE, WIDTH + SPACE);

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
  public VoxelShape getShape(
      BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    switch (state.get(BlockStateProperties.FACING)) {
      case DOWN:
      case UP:
        return STRAIGHT_SHAPE_UP_DOWN;
      case NORTH:
      case SOUTH:
        return STRAIGHT_SHAPE_NORTH_SOUTH;
      case WEST:
      case EAST:
        return STRAIGHT_SHAPE_EAST_WEST;
    }
    return super.getShape(state, worldIn, pos, context);
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
