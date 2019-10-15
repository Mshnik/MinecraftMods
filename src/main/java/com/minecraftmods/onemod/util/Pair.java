package com.minecraftmods.onemod.util;

import java.util.Objects;

/** @author Mshnik */
public final class Pair<A, B> {
  private final A a;
  private final B b;

  private Pair(A a, B b) {
    this.a = a;
    this.b = b;
  }

  public static <A, B> Pair<A,B> of(A a, B b) {
    return new Pair<>(a, b);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Pair)) {
      return false;
    }
    Pair p = (Pair) other;
    return Objects.equals(a, p.a) && Objects.equals(b, p.b);
  }

  @Override
  public int hashCode() {
    return (a != null ? a.hashCode() : 0) + 31 * (b != null ? b.hashCode() : 0);
  }

  @Override
  public String toString() {
    return String.format("(%s,%s)", a, b);
  }
}
