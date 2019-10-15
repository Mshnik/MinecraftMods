package com.minecraftmods.onemod.blocks;

import com.google.common.collect.ImmutableMap;
import com.minecraftmods.onemod.util.DirectionOrNone;
import com.minecraftmods.onemod.util.DirectionOrNoneProperty;
import com.minecraftmods.onemod.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.Direction.Axis.X;
import static net.minecraft.util.Direction.Axis.Y;
import static net.minecraft.util.Direction.Axis.Z;

/** @author Mshnik */
final class PipeBlock extends Block {
  private static final DirectionOrNoneProperty START = DirectionOrNoneProperty.allValues("start");
  private static final DirectionOrNoneProperty STOP = DirectionOrNoneProperty.allValues("stop");

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
    System.out.println("INIT - " + SHAPE_MAP);
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

  private boolean canAttach(IWorld world, BlockPos adjacentBlockPos) {
    BlockState blockstate = world.getBlockState(adjacentBlockPos);
    Block block = blockstate.getBlock();
    return block instanceof PipeBlock;
  }

  private <T> void addIf(List<T> list, T elem, boolean condition) {
    if (condition) {
      list.add(elem);
    }
  }

  private List<DirectionOrNone> getAttachedDirections(IWorld world, BlockPos blockPos) {
    ArrayList<DirectionOrNone> attachedDirections = new ArrayList<>();
    addIf(attachedDirections, DirectionOrNone.NORTH, canAttach(world, blockPos.north()));
    addIf(attachedDirections, DirectionOrNone.SOUTH, canAttach(world, blockPos.south()));
    addIf(attachedDirections, DirectionOrNone.EAST, canAttach(world, blockPos.east()));
    addIf(attachedDirections, DirectionOrNone.WEST, canAttach(world, blockPos.west()));
    addIf(attachedDirections, DirectionOrNone.UP, canAttach(world, blockPos.up()));
    addIf(attachedDirections, DirectionOrNone.DOWN, canAttach(world, blockPos.down()));
    return attachedDirections;
  }

  private BlockState updateState(IWorld world, BlockState current, BlockPos blockPos) {
    List<DirectionOrNone> connections = getAttachedDirections(world, blockPos);
    return current
        .with(START, connections.size() >= 1 ? connections.get(0) : DirectionOrNone.NORTH)
        .with(
            STOP,
            connections.size() >= 2
                ? connections.get(1)
                : connections.size() >= 1 ? connections.get(0) : DirectionOrNone.NORTH);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return updateState(context.getWorld(), super.getStateForPlacement(context), context.getPos());
  }

  @Override
  public void onBlockPlacedBy(
      World world,
      BlockPos blockPos,
      BlockState blockState,
      @Nullable LivingEntity entity,
      ItemStack stack) {}

  //  private static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
  //    return Direction.getFacingFromVector(
  //        (float) (entity.posX - clickedBlock.getX()),
  //        (float) (entity.posY - clickedBlock.getY()),
  //        (float) (entity.posZ - clickedBlock.getZ()));
  //  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(START, STOP);
  }

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
    return updateState(
        worldIn,
        super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos),
        currentPos);
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
