package com.minecraftmods.onemod.blocks;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

/** @author Mshnik */
public final class ModBlocks {
  private static ImmutableList<RegistryEntry<?>> REGISTRY_ENTRIES;
  private static ItemGroup ITEM_GROUP;

  private static boolean initted = false;

  @ObjectHolder("onemod:firstblock")
  static FirstBlock FIRST_BLOCK;

  /** Initializes the ModBlocks class. Only needs to be called once. Further calls no-op. */
  public static synchronized void init() {
    if (initted) {
      return;
    }

    // Initialize all mod blocks and item group.
    REGISTRY_ENTRIES = ImmutableList.of(RegistryEntry.of(FirstBlock.class, () -> FIRST_BLOCK));
    ITEM_GROUP = new MyModItemGroup();

    // Update group after ItemGroup is initted.
    REGISTRY_ENTRIES.forEach(r -> r.setItemProperties(r.getItemProperties().group(ITEM_GROUP)));

    initted = true;
  }

  /**
   * Returns a list of instances of each mod block. Blocks are instantiated in the same order they
   * are present in {@link #REGISTRY_ENTRIES}.
   */
  public static ImmutableList<Block> getModBlocks() {
    return REGISTRY_ENTRIES
        .stream()
        .map(ModBlocks::constructBlock)
        .collect(ImmutableList.toImmutableList());
  }

  /** Builds a {@link Block} from the given {@link RegistryEntry}. Exceptions are re-thrown. */
  private static <B extends Block> B constructBlock(RegistryEntry<B> registryEntry) {
    try {
      B block = registryEntry.getBlockClass().newInstance();
      block.setRegistryName(registryEntry.getRegistryName());
      return block;
    } catch (Exception e) {
      throw new RuntimeException("Error instantiating " + registryEntry.getBlockClass(), e);
    }
  }

  /**
   * Returns a list of instances of each mod block as {@link BlockItem}. Items are instantiated in
   * the same order they are present in {@link #REGISTRY_ENTRIES}.
   */
  public static ImmutableList<BlockItem> getModBlockItems() {
    return REGISTRY_ENTRIES
        .stream()
        .map(ModBlocks::constructBlockItem)
        .collect(ImmutableList.toImmutableList());
  }

  /** Builds a {@link BlockItem} from the given {@link RegistryEntry}. */
  private static BlockItem constructBlockItem(RegistryEntry<?> registryEntry) {
    BlockItem blockItem =
        new BlockItem(
            registryEntry.getObjectHolderSupplier().get(), registryEntry.getItemProperties());
    blockItem.setRegistryName(registryEntry.getRegistryName());
    return blockItem;
  }

  /** A helper class for representing the information necessary to register a block and item. */
  private static final class RegistryEntry<B extends Block> {

    /**
     * Constructs a new {@link RegistryEntry}. {@link #blockClass} and {@link #objectHolderSupplier}
     * are required args, the others will have default values.
     */
    private static <B extends Block> RegistryEntry<B> of(
        Class<B> blockClass, Supplier<B> objectHolderSupplier) {
      return new RegistryEntry<>(blockClass, objectHolderSupplier);
    }

    private final Class<B> blockClass;
    private final Supplier<B> objectHolderSupplier;
    private String registryName;
    private Item.Properties itemProperties;

    private RegistryEntry(Class<B> blockClass, Supplier<B> objectHolderSupplier) {
      this.blockClass = blockClass;
      this.objectHolderSupplier = objectHolderSupplier;
      registryName = blockClass.getSimpleName().toLowerCase();
      itemProperties = new Item.Properties();
    }

    /** Sets the {@link #registryName} and returns this. */
    private RegistryEntry<B> setRegistryName(String registryName) {
      this.registryName = registryName;
      return this;
    }

    /** Sets the {@link #itemProperties} and returns this. */
    private RegistryEntry<B> setItemProperties(Item.Properties itemProperties) {
      this.itemProperties = itemProperties;
      return this;
    }

    /** Returns {@link #blockClass}. */
    private Class<B> getBlockClass() {
      return blockClass;
    }

    /** Returns {@link #objectHolderSupplier}. */
    private Supplier<B> getObjectHolderSupplier() {
      return objectHolderSupplier;
    }

    /** Returns {@link #registryName}. */
    private String getRegistryName() {
      return registryName;
    }

    /** Returns {@link #itemProperties}. */
    private Item.Properties getItemProperties() {
      return itemProperties;
    }
  }

  /** {@link ItemGroup} for mod blocks in this class. */
  private static final class MyModItemGroup extends ItemGroup {

    private MyModItemGroup() {
      super("onemod");
    }

    @Override
    public ItemStack createIcon() {
      return new ItemStack(REGISTRY_ENTRIES.get(0).getObjectHolderSupplier().get());
    }
  }
}
