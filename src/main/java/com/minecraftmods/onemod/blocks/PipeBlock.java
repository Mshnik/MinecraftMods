package com.minecraftmods.onemod.blocks;

import com.google.common.collect.ImmutableMap;
import com.minecraftmods.onemod.util.DirectionPair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.DOWN;
import static net.minecraft.state.properties.BlockStateProperties.EAST;
import static net.minecraft.state.properties.BlockStateProperties.FACING;
import static net.minecraft.state.properties.BlockStateProperties.NORTH;
import static net.minecraft.state.properties.BlockStateProperties.SOUTH;
import static net.minecraft.state.properties.BlockStateProperties.UP;
import static net.minecraft.state.properties.BlockStateProperties.WEST;
import static net.minecraft.util.Direction.Axis.X;
import static net.minecraft.util.Direction.Axis.Y;
import static net.minecraft.util.Direction.Axis.Z;

/** @author Mshnik */
final class PipeBlock extends Block {
  private static final Properties PROPERTIES =
      Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(0.5f);

  private static final float BLOCK_START = 0;
  private static final float BLOCK_SIZE = 16;
  private static final float WIDTH = 6;
  private static final float SPACE = (BLOCK_SIZE - WIDTH) / 2;
  private static final float END_SIZE = BLOCK_SIZE - SPACE;

  private static ImmutableMap<DirectionPair, VoxelShape> SHAPE_MAP;

  private static VoxelShape createEndShape(Direction direction) {
    return Block.makeCuboidShape(0, 0, 0, 16, 16, 16);
  }

  private static VoxelShape createStraightShape(Direction direction) {
    Direction.Axis axis = direction.getAxis();
    return Block.makeCuboidShape(
        axis == X ? BLOCK_START : SPACE,
        axis == Y ? BLOCK_START : SPACE,
        axis == Z ? BLOCK_START : SPACE,
        axis == X ? BLOCK_SIZE : SPACE + WIDTH,
        axis == Y ? BLOCK_SIZE : SPACE + WIDTH,
        axis == Z ? BLOCK_SIZE : SPACE + WIDTH);
  }

  private static VoxelShape createCurveShape(Direction from, Direction to) {
    return Block.makeCuboidShape(0, 0, 0, 16, 16, 16);
  }

  static void init() {
    ImmutableMap.Builder<DirectionPair, VoxelShape> builder = ImmutableMap.builder();
    for (Direction d : Direction.values()) {
      builder.put(DirectionPair.of(d, d), createEndShape(d));
      builder.put(DirectionPair.of(d, d.getOpposite()), createStraightShape(d));
      for (Direction d2 : Direction.values()) {
        if (d2 != d && d2 != d.getOpposite()) {
          builder.put(DirectionPair.of(d, d2), createCurveShape(d, d2));
        }
      }
    }
    SHAPE_MAP = builder.build();
  }

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

  private boolean canAttach(BlockItemUseContext context, BlockPos adjacentBlockPos) {
    BlockState blockstate = context.getWorld().getBlockState(adjacentBlockPos);
    Block block = blockstate.getBlock();
    return block instanceof PipeBlock;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    BlockPos blockpos = context.getPos();
    return super.getStateForPlacement(context)
        .with(NORTH, canAttach(context, blockpos.north()))
        .with(EAST, canAttach(context, blockpos.east()))
        .with(SOUTH, canAttach(context, blockpos.south()))
        .with(WEST, canAttach(context, blockpos.west()))
        .with(UP, canAttach(context, blockpos.up()))
        .with(DOWN, canAttach(context, blockpos.down()));
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
          blockPos, blockState.with(FACING, getFacingFromEntity(blockPos, entity)), 2);
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
    builder.add(FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN);
  }

  @Override
  public VoxelShape getShape(
      BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    switch (state.get(FACING)) {
      case DOWN:
      case UP:
        return SHAPE_MAP.get(DirectionPair.of(Direction.DOWN, Direction.UP));
      case NORTH:
      case SOUTH:
        return SHAPE_MAP.get(DirectionPair.of(Direction.NORTH, Direction.SOUTH));
      case WEST:
      case EAST:
        return SHAPE_MAP.get(DirectionPair.of(Direction.EAST, Direction.WEST));
    }
    return super.getShape(state, worldIn, pos, context);
  }

  @Override
  public BlockState updatePostPlacement(
      BlockState stateIn,
      Direction facing,
      BlockState facingState,
      IWorld worldIn,
      BlockPos currentPos,
      BlockPos facingPos) {
    return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
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
        // NetworkHooks.openGui(
        // (ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
      }
    }
    return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
  }
}
