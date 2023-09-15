/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 pcal.net
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
import static net.pcal.copperhopper.CopperHopperMod.COHO_BLOCK_ENTITY_TYPE_ID;
import static net.pcal.copperhopper.CopperHopperMod.COHO_BLOCK_ID;
import static net.pcal.copperhopper.CopperHopperMod.COHO_ITEM_ID;
import static net.pcal.copperhopper.CopperHopperMod.COHO_MINECART_ENTITY_TYPE_ID;
import static net.pcal.copperhopper.CopperHopperMod.COHO_MINECART_ITEM_ID;
import static net.pcal.copperhopper.CopperHopperMod.COHO_SCREEN_ID;
import static net.pcal.copperhopper.CopperHopperMod.LOGGER_NAME;
import static net.pcal.copperhopper.CopperHopperMod.LOG_PREFIX;
import static net.pcal.copperhopper.CopperHopperMod.mod;

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
        HandledScreens.register(mod().getScreenHandlerType(), CohoScreen::new);
    }


    private static class ExactlyOnceInitializer {
        static {
            final Logger logger = LogManager.getLogger(LOGGER_NAME);
            try {
                final Properties config;
                mod().createDefaultConfig();
                config = mod().loadConfig();
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

            //
            // Register the Screen
            //
            register(Registries.SCREEN_HANDLER, COHO_SCREEN_ID, new ScreenHandlerType<>(CohoScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

            //
            // Register the Block
            //
            final CopperHopperBlock cohoBlock = new CopperHopperBlock(CopperHopperBlock.getDefaultSettings());
            final CopperHopperItem cohoItem = new CopperHopperItem(cohoBlock, new Item.Settings());
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.addAfter(Items.HOPPER, cohoItem));

            cohoItem.appendBlocks(Item.BLOCK_ITEMS, cohoItem); // wat
            register(Registries.BLOCK_ENTITY_TYPE, COHO_BLOCK_ENTITY_TYPE_ID,
                    FabricBlockEntityTypeBuilder.create(CopperHopperBlockEntity::new, cohoBlock).build(null));
            register(Registries.ITEM, COHO_ITEM_ID, cohoItem);
            register(Registries.BLOCK, COHO_BLOCK_ID, cohoBlock);

            //
            // Register the Minecart
            //
            final EntityType<CopperHopperMinecartEntity> minecartType = FabricEntityTypeBuilder.<CopperHopperMinecartEntity>create(SpawnGroup.MISC, CopperHopperMinecartEntity::new).
                    dimensions(EntityDimensions.fixed(0.98f, 0.7f)).build();
            final CopperHopperMinecartItem cohoMinecartItem = new CopperHopperMinecartItem(new Item.Settings().maxCount(1));
            register(Registries.ENTITY_TYPE, COHO_MINECART_ENTITY_TYPE_ID, minecartType);
            register(Registries.ITEM, COHO_MINECART_ITEM_ID, cohoMinecartItem);
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.addAfter(Items.HOPPER_MINECART, cohoMinecartItem));
            EntityRendererRegistry.register(minecartType, ctx -> new MinecartEntityRenderer<>(ctx, EntityModelLayers.HOPPER_MINECART));
        }
    }
}
