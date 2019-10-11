package com.minecraftmods.onemod;

import com.minecraftmods.onemod.blocks.ModBlocks;
import com.minecraftmods.onemod.setup.ClientProxy;
import com.minecraftmods.onemod.setup.IProxy;
import com.minecraftmods.onemod.setup.ModSetup;
import com.minecraftmods.onemod.setup.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("onemod")
public final class OneMod {
  public static final String MODID = "onemod";

  // Don't replace with method reference - subtle differences with class loading.
  private static final IProxy proxy =
      DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

  private static final ModSetup modSetup = new ModSetup();

  // Directly reference a log4j logger.
  private static final Logger LOGGER = LogManager.getLogger();

  public OneMod() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

    // Init ModBlocks.
    ModBlocks.init();
  }

  private void setup(final FMLCommonSetupEvent event) {
    // Invoked after all blocks are registered.
    modSetup.init();
    proxy.init();
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this
  // is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {
    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
      LOGGER.info("HELLO from Register Block");
      ModBlocks.getModBlocks().forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
      LOGGER.info("HELLO from Register Item");
      ModBlocks.getModBlockItems().forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
      LOGGER.info("HELLO from Register Tile Entity");
      ModBlocks.getTileEntityTypes().forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
      LOGGER.info("HELLO from Register Container Type");
      ModBlocks.getContainerTypes(proxy::getClientWorld).forEach(event.getRegistry()::register);
    }
  }
}
