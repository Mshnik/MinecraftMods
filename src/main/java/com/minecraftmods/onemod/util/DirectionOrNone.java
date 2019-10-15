package com.minecraftmods.onemod.util;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;
import java.util.Optional;

/** @author Mshnik */
public enum DirectionOrNone implements IStringSerializable {
  NORTH(Direction.NORTH, 1),
  SOUTH(Direction.SOUTH, 0),
  EAST(Direction.EAST, 3),
  WEST(Direction.WEST, 2),
  UP(Direction.UP, 5),
  DOWN(Direction.DOWN, 4),
  NONE(null, 6);

  public static DirectionOrNone[] valuesNoNone() {
    return new DirectionOrNone[] {NORTH, SOUTH, EAST, WEST, UP, DOWN};
  }

  @Nullable private final Direction direction;
  private final int oppositeIndex;

  private DirectionOrNone(@Nullable Direction direction, int oppositeIndex) {
    this.direction = direction;
    this.oppositeIndex = oppositeIndex;
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

  @Override
  public String getName() {
    return super.name().toLowerCase();
  }
}
