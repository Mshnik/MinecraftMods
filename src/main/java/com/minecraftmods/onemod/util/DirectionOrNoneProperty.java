package com.minecraftmods.onemod.util;

import com.google.common.collect.ImmutableSet;
import net.minecraft.state.EnumProperty;

import java.util.Collection;

/** @author Mshnik */
public final class DirectionOrNoneProperty extends EnumProperty<DirectionOrNone> {
  private DirectionOrNoneProperty(String name, Collection<DirectionOrNone> values) {
    super(name, DirectionOrNone.class, values);
  }

  public static DirectionOrNoneProperty allValues(String name) {
    return new DirectionOrNoneProperty(name, ImmutableSet.copyOf(DirectionOrNone.values()));
  }
}
