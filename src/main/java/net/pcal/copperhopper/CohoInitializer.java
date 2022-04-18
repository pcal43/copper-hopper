package net.pcal.copperhopper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

import static net.minecraft.util.registry.Registry.register;
import static net.pcal.copperhopper.CohoService.COHO_BLOCK_ENTITY_TYPE_ID;
import static net.pcal.copperhopper.CohoService.COHO_BLOCK_ID;
import static net.pcal.copperhopper.CohoService.COHO_ITEM_ID;
import static net.pcal.copperhopper.CohoService.COHO_SCREEN_ID;
import static net.pcal.copperhopper.CohoService.LOGGER_NAME;
import static net.pcal.copperhopper.CohoService.LOG_PREFIX;

public class CohoInitializer implements ModInitializer, ClientModInitializer {

    @Override
    public void onInitialize() {
        new ExactlyOnceInitializer();
    }

    @Override
    public void onInitializeClient() {
        new ExactlyOnceInitializer();
        ScreenHandlerRegistry.registerSimple(COHO_SCREEN_ID, CohoScreen::register);
    }


    private static class ExactlyOnceInitializer {
        static {
            final Logger logger = LogManager.getLogger(LOGGER_NAME);
            try {
                final Properties config;
                CohoService.getInstance().createDefaultConfig();
                config = CohoService.getInstance().loadConfig();
                if ("true".equals(config.getProperty("polymer-enabled"))) {
                    logger.info("Initializing polymer.");
                    ((Runnable) Class.forName("net.pcal.copperhopper.polymer.PolymerRegistrar").getDeclaredConstructor().newInstance()).run();
                } else {
                    doStandardRegistrations();
                }
                logger.info(LOG_PREFIX + "Initialized.");
            } catch (Exception e) {
                logger.catching(Level.ERROR, e);
                logger.error(LOG_PREFIX + "Failed to initialize");
            }
        }

        /**
         * Create and register all of our blocks and items for non-polymer mode.
         */
        private static void doStandardRegistrations() {
            ScreenHandlerRegistry.registerSimple(COHO_SCREEN_ID, CohoScreenHandler::new);
            final CopperHopperBlock cohoBlock = new CopperHopperBlock(CopperHopperBlock.getDefaultSettings());
            final CopperHopperItem cohoItem = new CopperHopperItem(cohoBlock, new Item.Settings().group(ItemGroup.REDSTONE));
            cohoItem.appendBlocks(Item.BLOCK_ITEMS, cohoItem); // wat
            register(Registry.BLOCK_ENTITY_TYPE, COHO_BLOCK_ENTITY_TYPE_ID,
                    FabricBlockEntityTypeBuilder.create(CopperHopperBlockEntity::new, cohoBlock).build(null));
            register(Registry.ITEM, COHO_ITEM_ID, cohoItem);
            register(Registry.BLOCK, COHO_BLOCK_ID, cohoBlock);
        }
    }
}
