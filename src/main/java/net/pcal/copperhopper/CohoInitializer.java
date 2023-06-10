package net.pcal.copperhopper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

import static net.minecraft.registry.Registry.register;
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
        HandledScreens.register(CohoService.getScreenHandlerType(), CohoScreen::new);
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
            final CopperHopperBlock cohoBlock = new CopperHopperBlock(CopperHopperBlock.getDefaultSettings());

            final CopperHopperItem cohoItem = new CopperHopperItem(cohoBlock, new Item.Settings());
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.add(cohoItem));

            cohoItem.appendBlocks(Item.BLOCK_ITEMS, cohoItem); // wat
            register(Registries.BLOCK_ENTITY_TYPE, COHO_BLOCK_ENTITY_TYPE_ID,
                    FabricBlockEntityTypeBuilder.create(CopperHopperBlockEntity::new, cohoBlock).build(null));
            register(Registries.ITEM, COHO_ITEM_ID, cohoItem);
            register(Registries.BLOCK, COHO_BLOCK_ID, cohoBlock);
            register(Registries.SCREEN_HANDLER, COHO_SCREEN_ID, new ScreenHandlerType(CohoScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
        }
    }
}
