package com.minecraftmods.onemod.util;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

/** @author Mshnik */
public enum DirectionOrNone implements IStringSerializable {
  NONE(null, 0, Function.identity()),
  NORTH(Direction.NORTH, 2, BlockPos::north),
  SOUTH(Direction.SOUTH, 1, BlockPos::south),
  EAST(Direction.EAST, 4, BlockPos::east),
  WEST(Direction.WEST, 3, BlockPos::west),
  UP(Direction.UP, 6, BlockPos::up),
  DOWN(Direction.DOWN, 5, BlockPos::down);

  public static DirectionOrNone[] valuesNoNone() {
    return new DirectionOrNone[] {NORTH, SOUTH, EAST, WEST, UP, DOWN};
  }

  @Nullable private final Direction direction;
  private final int oppositeIndex;
  private final Function<BlockPos, BlockPos> blockPosMoveFunction;

  private DirectionOrNone(
      @Nullable Direction direction,
      int oppositeIndex,
      Function<BlockPos, BlockPos> blockPosMoveFunction) {
    this.direction = direction;
    this.oppositeIndex = oppositeIndex;
    this.blockPosMoveFunction = blockPosMoveFunction;
  }

  public DirectionOrNone getOpposite() {
    return values()[oppositeIndex];
  }

  public Optional<Direction> asDirection() {
    return Optional.ofNullable(direction);
  }

  public boolean isNone() {
    return !asDirection().isPresent();
  }

  public BlockPos moveBlockPos(BlockPos current) {
    return blockPosMoveFunction.apply(current);
  }

  @Override
  public String getName() {
    return super.name().toLowerCase();
  }
}
