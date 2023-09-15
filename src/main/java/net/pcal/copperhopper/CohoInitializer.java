package net.pcal.copperhopper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
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
import static net.pcal.copperhopper.CohoService.COHO_MINECART_ENTITY_TYPE_ID;
import static net.pcal.copperhopper.CohoService.COHO_MINECART_ITEM_ID;
import static net.pcal.copperhopper.CohoService.COHO_SCREEN_ID;
import static net.pcal.copperhopper.CohoService.LOGGER_NAME;
import static net.pcal.copperhopper.CohoService.LOG_PREFIX;
import static net.pcal.copperhopper.CohoService.getInstance;

/**
 * @author pcal
 * @since 0.0.1
 */
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
                getInstance().createDefaultConfig();
                config = getInstance().loadConfig();
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

            register(Registries.SCREEN_HANDLER, COHO_SCREEN_ID, new ScreenHandlerType<>(CohoScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

            final CopperHopperBlock cohoBlock = new CopperHopperBlock(CopperHopperBlock.getDefaultSettings());
            final CopperHopperItem cohoItem = new CopperHopperItem(cohoBlock, new Item.Settings());
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.addAfter(Items.HOPPER, cohoItem));

            cohoItem.appendBlocks(Item.BLOCK_ITEMS, cohoItem); // wat
            register(Registries.BLOCK_ENTITY_TYPE, COHO_BLOCK_ENTITY_TYPE_ID,
                    FabricBlockEntityTypeBuilder.create(CopperHopperBlockEntity::new, cohoBlock).build(null));
            register(Registries.ITEM, COHO_ITEM_ID, cohoItem);
            register(Registries.BLOCK, COHO_BLOCK_ID, cohoBlock);

            final EntityType<CopperHopperMinecartEntity> minecartType = FabricEntityTypeBuilder.<CopperHopperMinecartEntity>create(SpawnGroup.MISC, CopperHopperMinecartEntity::new).
                    dimensions(EntityDimensions.fixed(0.98f, 0.7f)).build();
            final CopperHopperMinecartItem cohoMinecartItem = new CopperHopperMinecartItem(new Item.Settings().maxCount(1));
            register(Registries.ENTITY_TYPE, COHO_MINECART_ENTITY_TYPE_ID, minecartType);
            register(Registries.ITEM, COHO_MINECART_ITEM_ID, cohoMinecartItem);
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.addAfter(Items.HOPPER_MINECART, cohoMinecartItem));

            EntityRendererRegistry.register(minecartType, ctx -> new MinecartEntityRenderer<>(ctx, EntityModelLayers.HOPPER_MINECART));

            //HOPPER_MINECART = register("hopper_minecart",EntityType.Builder.create(HopperMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98F, 0.7F).maxTrackingRange(8));


        }
    }
}
