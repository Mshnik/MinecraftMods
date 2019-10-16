package com.minecraftmods.onemod.blocks;

import com.google.common.collect.ImmutableSet;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

import java.util.Collection;

/** @author Mshnik */
public enum PipeFlow implements IStringSerializable {
  NONE,
  TOWARDS_START,
  TOWARDS_STOP;

  @Override
  public String getName() {
    return super.name().toLowerCase();
  }

  public static final class PipeFlowProperty extends EnumProperty<PipeFlow> {
    private PipeFlowProperty(String name, Collection<PipeFlow> values) {
      super(name, PipeFlow.class, values);
    }

    public static PipeFlowProperty allValues(String name) {
      return new PipeFlowProperty(name, ImmutableSet.copyOf(values()));
    }
  }
}
