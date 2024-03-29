package com.minecraftmods.onemod.blocks;

import com.google.common.collect.ImmutableMap;
import com.minecraftmods.onemod.util.DirectionOrNone;
import com.minecraftmods.onemod.util.DirectionOrNoneProperty;
import com.minecraftmods.onemod.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static net.minecraft.util.Direction.Axis.X;
import static net.minecraft.util.Direction.Axis.Y;
import static net.minecraft.util.Direction.Axis.Z;

/** @author Mshnik */
final class PipeBlock extends ContainerBlock {
  private static final boolean IS_DEBUG = true;

  static final BooleanProperty DEBUG = BooleanProperty.create("debug");
  static final DirectionOrNoneProperty START = DirectionOrNoneProperty.allValues("start");
  static final DirectionOrNoneProperty STOP = DirectionOrNoneProperty.allValues("stop");

  private static final Properties PROPERTIES =
      Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(0.5f);

  private static final float BLOCK_START = 0;
  private static final float BLOCK_SIZE = 16;
  private static final float WIDTH = 6;
  private static final float SPACE = (BLOCK_SIZE - WIDTH) / 2;

  private static ImmutableMap<Pair<DirectionOrNone, DirectionOrNone>, VoxelShape> SHAPE_MAP;

  private static VoxelShape createEndShape(Direction direction) {
    boolean endOnMinSide =
        direction == Direction.NORTH || direction == Direction.WEST || direction == Direction.DOWN;
    Direction.Axis axis = direction.getAxis();
    return Block.makeCuboidShape(
        axis == X && endOnMinSide ? BLOCK_START : SPACE,
        axis == Y && endOnMinSide ? BLOCK_START : SPACE,
        axis == Z && endOnMinSide ? BLOCK_START : SPACE,
        axis == X && !endOnMinSide ? BLOCK_SIZE : SPACE + WIDTH,
        axis == Y && !endOnMinSide ? BLOCK_SIZE : SPACE + WIDTH,
        axis == Z && !endOnMinSide ? BLOCK_SIZE : SPACE + WIDTH);
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
    return VoxelShapes.or(createEndShape(from), createEndShape(to));
  }

  static void init() {
    ImmutableMap.Builder<Pair<DirectionOrNone, DirectionOrNone>, VoxelShape> builder =
        ImmutableMap.builder();

    // None-None element.
    builder.put(
        Pair.of(DirectionOrNone.NONE, DirectionOrNone.NONE),
        Block.makeCuboidShape(SPACE, SPACE, SPACE, SPACE + WIDTH, SPACE + WIDTH, SPACE + WIDTH));

    // Each direction shapes.
    for (DirectionOrNone d : DirectionOrNone.valuesNoNone()) {
      VoxelShape endShape = createEndShape(d.asDirection().get());

      // End shapes.
      builder.put(Pair.of(d, DirectionOrNone.NONE), endShape);
      builder.put(Pair.of(DirectionOrNone.NONE, d), endShape);
      builder.put(Pair.of(d, d), endShape);

      // Straight.
      builder.put(Pair.of(d, d.getOpposite()), createStraightShape(d.asDirection().get()));

      // Curves.
      for (DirectionOrNone d2 : DirectionOrNone.valuesNoNone()) {
        if (d2 != d && d2 != d.getOpposite()) {
          builder.put(
              Pair.of(d, d2), createCurveShape(d.asDirection().get(), d2.asDirection().get()));
        }
      }
    }
    SHAPE_MAP = builder.build();
  }

  PipeBlock() {
    super(PROPERTIES);
    setDefaultState(getDefaultState().with(DEBUG, IS_DEBUG));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(START, STOP, DEBUG);
  }

  @Override
  public boolean hasTileEntity(BlockState blockState) {
    return true;
  }

  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new PipeBlockTile();
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  private void addIfCanAttach(
      Collection<DirectionOrNone> list,
      IWorld world,
      DirectionOrNone direction,
      BlockPos blockPos) {
    BlockState blockstate = world.getBlockState(direction.moveBlockPos(blockPos));
    if (blockstate.getBlock() instanceof HopperBlock
        || (blockstate.getBlock() instanceof PipeBlock
            && (blockstate.get(START).isNone()
                || blockstate.get(STOP).isNone()
                || blockstate.get(START) == direction.getOpposite()
                || blockstate.get(STOP) == direction.getOpposite()))) {
      list.add(direction);
    }
  }

  private Set<DirectionOrNone> getAttachedDirections(IWorld world, BlockPos blockPos) {
    HashSet<DirectionOrNone> attachedDirections = new HashSet<>();
    addIfCanAttach(attachedDirections, world, DirectionOrNone.NORTH, blockPos);
    addIfCanAttach(attachedDirections, world, DirectionOrNone.SOUTH, blockPos);
    addIfCanAttach(attachedDirections, world, DirectionOrNone.EAST, blockPos);
    addIfCanAttach(attachedDirections, world, DirectionOrNone.WEST, blockPos);
    addIfCanAttach(attachedDirections, world, DirectionOrNone.UP, blockPos);
    addIfCanAttach(attachedDirections, world, DirectionOrNone.DOWN, blockPos);
    return attachedDirections;
  }

  private BlockState syncFlowDirection(IWorld world, BlockState current, BlockPos blockPos) {
    DirectionOrNone start = current.get(START);
    DirectionOrNone stop = current.get(STOP);

    if (start.isNone() && stop.isNone()) {
      return current;
    }

    BlockState neighborBlockStartDirection = world.getBlockState(start.moveBlockPos(blockPos));
    BlockState neighborBlockStopDirection = world.getBlockState(stop.moveBlockPos(blockPos));

    if (neighborBlockStartDirection.get(START).getOpposite().equals(start)
        && neighborBlockStopDirection.get(STOP).getOpposite().equals(stop)) {
      return current.with(START, stop).with(STOP, start);
    } else {
      return current;
    }
  }

  private BlockState updateAttachState(IWorld world, BlockState current, BlockPos blockPos) {
    DirectionOrNone currentStart = current.get(START);
    DirectionOrNone currentStop = current.get(STOP);
    Set<DirectionOrNone> connections = getAttachedDirections(world, blockPos);

    DirectionOrNone newStart = DirectionOrNone.NONE;
    DirectionOrNone newStop = DirectionOrNone.NONE;

    // If existing connections still exist, use them.
    if (connections.contains(currentStart)) {
      newStart = currentStart;
      connections.remove(currentStart);
    }
    if (connections.contains(currentStop)) {
      newStop = currentStop;
      connections.remove(currentStop);
    }

    // If directions are still none, try to pick a from remainder..
    if (newStart == DirectionOrNone.NONE) {
      newStart = connections.stream().findFirst().orElse(DirectionOrNone.NONE);
      connections.remove(newStart);
    }
    if (newStop == DirectionOrNone.NONE) {
      newStop = connections.stream().findFirst().orElse(DirectionOrNone.NONE);
      connections.remove(newStop);
    }

    BlockState updated = current.with(START, newStart).with(STOP, newStop);
    return syncFlowDirection(world, updated, blockPos);
  }

  private Optional<BlockState> getBlockInDirection(
      World world, BlockPos currentPos, DirectionOrNone directionOrNone) {
    return directionOrNone
        .asDirection()
        .map(d -> currentPos.add(d.getDirectionVec()))
        .map(world::getBlockState);
  }

  private boolean connectedToAdjacentHopperInDirection(
      World world, BlockState current, BlockPos currentPos, DirectionOrNone directionOrNone) {
    return (current.get(START) == directionOrNone || current.get(STOP) == directionOrNone)
        && world.getBlockState(directionOrNone.moveBlockPos(currentPos)).getBlock()
            instanceof HopperBlock;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return updateAttachState(
        context.getWorld(), super.getStateForPlacement(context), context.getPos());
  }

  @Override
  public void onBlockPlacedBy(
      World world,
      BlockPos blockPos,
      BlockState blockState,
      @Nullable LivingEntity entity,
      ItemStack stack) {}

  @Override
  public VoxelShape getShape(
      BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPE_MAP.get(Pair.of(state.get(START), state.get(STOP)));
  }

  @Override
  public BlockState updatePostPlacement(
      BlockState stateIn,
      Direction facing,
      BlockState facingState,
      IWorld worldIn,
      BlockPos currentPos,
      BlockPos facingPos) {
    // Improve to only consider facing direction.
    BlockState updatedAttachState =
        updateAttachState(
            worldIn,
            super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos),
            currentPos);
    return updatedAttachState;
  }

  @Override
  public void neighborChanged(
      BlockState state,
      World worldIn,
      BlockPos pos,
      Block blockIn,
      BlockPos fromPos,
      boolean isMoving) {
    if (!worldIn.isRemote) {}
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
      if (tileEntity instanceof PipeBlockTile) {
        PipeBlockTile provider = (PipeBlockTile) tileEntity;
        // if (provider.hasItem()) {
        // Player cast is safe because world is not remote.
        NetworkHooks.openGui((ServerPlayerEntity) player, provider, tileEntity.getPos());
        // }
      }
    }
    return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
  }
}
