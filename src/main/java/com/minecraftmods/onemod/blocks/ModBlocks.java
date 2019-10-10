package com.minecraftmods.onemod.blocks;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.function.Supplier;

/** @author Mshnik */
public final class ModBlocks {
  private static ImmutableList<RegistryEntry<?, ?>> REGISTRY_ENTRIES;
  private static ItemGroup ITEM_GROUP;

  private static boolean initted = false;

  /** Initializes the ModBlocks class. Only needs to be called once. Further calls no-op. */
  public static synchronized void init() {
    if (initted) {
      return;
    }

    // Initialize all mod blocks and item group.
    REGISTRY_ENTRIES =
        ImmutableList.of(
            RegistryEntry.of(
                FirstBlock.class, () -> ObjectHolders.FIRST_BLOCK, FirstBlockTile::new));
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
        .map(RegistryEntry::toBlock)
        .collect(ImmutableList.toImmutableList());
  }

  /**
   * Returns a list of instances of each mod block as {@link BlockItem}. Items are instantiated in
   * the same order they are present in {@link #REGISTRY_ENTRIES}.
   */
  public static ImmutableList<BlockItem> getModBlockItems() {
    return REGISTRY_ENTRIES
        .stream()
        .map(RegistryEntry::toBlockItem)
        .collect(ImmutableList.toImmutableList());
  }

  /**
   * Returns a list of {@link TileEntityType}s. Types are instantiated in the same order they are
   * present in {@link #REGISTRY_ENTRIES}.
   */
  public static ImmutableList<TileEntityType<?>> getTileEntityTypes() {
    return REGISTRY_ENTRIES
        .stream()
        .map(RegistryEntry::toTileEntityType)
        .collect(ImmutableList.toImmutableList());
  }

  /** A helper class for representing the information necessary to register a block and item. */
  private static final class RegistryEntry<B extends Block, T extends TileEntity> {

    /**
     * Constructs a new {@link RegistryEntry}. {@link #blockClass} and {@link #objectHolderSupplier}
     * are required args, the others will have default values.
     */
    private static <B extends Block, T extends TileEntity> RegistryEntry<B, T> of(
        Class<B> blockClass, Supplier<B> objectHolderSupplier, Supplier<T> tileEntitySupplier) {
      return new RegistryEntry<>(blockClass, objectHolderSupplier, tileEntitySupplier);
    }

    private final Class<B> blockClass;
    private final Supplier<B> objectHolderSupplier;
    private final Supplier<T> tileEntitySupplier;

    private String registryName;
    private Item.Properties itemProperties;

    private RegistryEntry(
        Class<B> blockClass, Supplier<B> objectHolderSupplier, Supplier<T> tileEntitySupplier) {
      this.blockClass = blockClass;
      this.objectHolderSupplier = objectHolderSupplier;
      registryName = blockClass.getSimpleName().toLowerCase();
      this.tileEntitySupplier = tileEntitySupplier;
      itemProperties = new Item.Properties();
    }

    /** Sets the {@link #registryName} and returns this. */
    private RegistryEntry<B, T> setRegistryName(String registryName) {
      this.registryName = registryName;
      return this;
    }

    /** Sets the {@link #itemProperties} and returns this. */
    private RegistryEntry<B, T> setItemProperties(Item.Properties itemProperties) {
      this.itemProperties = itemProperties;
      return this;
    }

    /** Returns {@link #objectHolderSupplier}. */
    private Supplier<B> getObjectHolderSupplier() {
      return objectHolderSupplier;
    }

    /** Returns {@link #itemProperties}. */
    private Item.Properties getItemProperties() {
      return itemProperties;
    }

    /** Builds a {@link Block} from this {@link RegistryEntry}. Exceptions are re-thrown. */
    private B toBlock() {
      try {
        B block = blockClass.newInstance();
        block.setRegistryName(registryName);
        return block;
      } catch (Exception e) {
        throw new RuntimeException("Error instantiating " + blockClass.getCanonicalName(), e);
      }
    }

    /** Builds a {@link BlockItem} from this {@link RegistryEntry}. */
    private BlockItem toBlockItem() {
      BlockItem blockItem = new BlockItem(objectHolderSupplier.get(), itemProperties);
      blockItem.setRegistryName(registryName);
      return blockItem;
    }

    /** Builds a {@link TileEntityType} from this {@link RegistryEntry}. */
    private TileEntityType<T> toTileEntityType() {
      TileEntityType<T> tileEntityType =
          TileEntityType.Builder.create(tileEntitySupplier, objectHolderSupplier.get()).build(null);
      tileEntityType.setRegistryName(registryName);
      return tileEntityType;
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
