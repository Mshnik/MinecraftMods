package com.minecraftmods.onemod.util;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/** @author Mshnik */
public final class DirectionPair {
  private static final Map<Direction, Map<Direction, DirectionPair>> typeMap;

  static {
    typeMap = new HashMap<>();
    for (Direction d1 : Direction.values()) {
      typeMap.put(d1, new HashMap<>());
      for (Direction d2 : Direction.values()) {
        typeMap.get(d1).put(d2, new DirectionPair(d1, d2));
      }
    }
  }

  public static DirectionPair of(
      @Nonnull Direction startDirection, @Nonnull Direction stopDirection) {
    return typeMap.get(startDirection).get(stopDirection);
  }

  public final Direction startDirection;
  public final Direction stopDirection;

  private DirectionPair(Direction startDirection, Direction stopDirection) {
    this.startDirection = startDirection;
    this.stopDirection = stopDirection;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof DirectionPair)) {
      return false;
    }
    DirectionPair other = (DirectionPair) o;
    return startDirection == other.startDirection && stopDirection == other.stopDirection;
  }

  @Override
  public int hashCode() {
    return startDirection.hashCode() + 31 * stopDirection.hashCode();
  }

  @Override
  public String toString() {
    return "{" + startDirection + "," + stopDirection + "}";
  }
}
