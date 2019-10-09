package com.example.examplemod;

import com.example.examplemod.blocks.FirstBlock;
import com.example.examplemod.blocks.ModBlocks;
import com.example.examplemod.setup.ClientProxy;
import com.example.examplemod.setup.IProxy;
import com.example.examplemod.setup.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("examplemod")
public final class ExampleMod {
  // Don't replace with method reference - subtle differences with class loading.
  public static final IProxy proxy =
      DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

  // Directly reference a log4j logger.
  private static final Logger LOGGER = LogManager.getLogger();

  public ExampleMod() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
  }

  private void setup(final FMLCommonSetupEvent event) {
    // Invoked after all blocks are registered.
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this
  // is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {
    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
      // register a new block here
      LOGGER.info("HELLO from Register Block");
      blockRegistryEvent.getRegistry().register(new FirstBlock());
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> blockRegistryEvent) {
      // register a new item here
      LOGGER.info("HELLO from Register Item");
      blockRegistryEvent
          .getRegistry()
          .register(
              new BlockItem(ModBlocks.FIRST_BLOCK, new Item.Properties())
                  .setRegistryName(FirstBlock.REGISTRY_NAME));
    }
  }
}
