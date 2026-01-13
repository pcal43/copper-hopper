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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static net.minecraft.core.Registry.register;
import static net.minecraft.core.registries.BuiltInRegistries.BLOCK;
import static net.pcal.copperhopper.CopperHopperMod.COHO_BLOCK_ENTITY_TYPE_ID;
import static net.pcal.copperhopper.CopperHopperMod.COHO_BLOCK_IDS;
import static net.pcal.copperhopper.CopperHopperMod.COHO_MINECART_ENTITY_TYPE_ID;
import static net.pcal.copperhopper.CopperHopperMod.COHO_MINECART_ITEM_ID;
import static net.pcal.copperhopper.CopperHopperMod.COHO_SCREEN_ID;
import static net.pcal.copperhopper.CopperHopperMod.COPPER_HOPPER;
import static net.pcal.copperhopper.CopperHopperMod.EXPOSED_COPPER_HOPPER;
import static net.pcal.copperhopper.CopperHopperMod.LOGGER_NAME;
import static net.pcal.copperhopper.CopperHopperMod.LOG_PREFIX;
import static net.pcal.copperhopper.CopperHopperMod.OXIDIZED_COPPER_HOPPER;
import static net.pcal.copperhopper.CopperHopperMod.WEATHERED_COPPER_HOPPER;
import static net.pcal.copperhopper.CopperHopperMod.WAXED_COPPER_HOPPER;
import static net.pcal.copperhopper.CopperHopperMod.WAXED_EXPOSED_COPPER_HOPPER;
import static net.pcal.copperhopper.CopperHopperMod.WAXED_WEATHERED_COPPER_HOPPER;
import static net.pcal.copperhopper.CopperHopperMod.WAXED_OXIDIZED_COPPER_HOPPER;
import static net.pcal.copperhopper.CopperHopperMod.mod;

/**
 * @author pcal
 * @since 0.0.1
 */
public class CohoInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        new ExactlyOnceInitializer();
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
                // We should abort minecraft startup.  Otherwise, existing copper hoppers may be removed from the world as 
                // invalid blocks.  If that's what they want, they can just disable the mod.
                throw new RuntimeException(e); 
            }
        }

        /**
         * Create and register all of our blocks and items for non-polymer mode.
         */
        private static void doStandardRegistrations() {
            //
            // Register the Screen
            //
            register(BuiltInRegistries.MENU, COHO_SCREEN_ID, new MenuType<>(CohoScreenHandler::new, FeatureFlags.VANILLA_SET));

            //
            // Register the Blocks and Items
            //
            final List<CopperHopperBlock> cohoBlocks = new ArrayList<>();
            for (final Tuple<Identifier, WeatherState> tuple : COHO_BLOCK_IDS) {
                final Identifier blockId = tuple.getA();
                final WeatherState weatherState = tuple.getB();
                final Identifier itemId = blockId;
                final CopperHopperBlock cohoBlock = new CopperHopperBlock(weatherState, CopperHopperBlock.getDefaultSettings(blockId));
                cohoBlocks.add(cohoBlock);
                final ResourceKey<Item> itemReourceKey = ResourceKey.create(Registries.ITEM, itemId);
                final CopperHopperItem cohoItem = new CopperHopperItem(cohoBlock, new Item.Properties().setId(itemReourceKey).useBlockDescriptionPrefix());
                ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> entries.addAfter(Items.HOPPER, cohoItem));

                cohoItem.registerBlocks(Item.BY_BLOCK, cohoItem); // wat
                register(BuiltInRegistries.ITEM, itemId, cohoItem);
                register(BLOCK, blockId, cohoBlock);
            }

            register(BuiltInRegistries.BLOCK_ENTITY_TYPE, COHO_BLOCK_ENTITY_TYPE_ID,
                    FabricBlockEntityTypeBuilder.create(CopperHopperBlockEntity::new,
                            cohoBlocks.toArray(new CopperHopperBlock[0])).build());

            //
            // Register the Minecart
            //
            final ResourceKey<EntityType<?>> minecartResourceKey = ResourceKey.create(Registries.ENTITY_TYPE, CopperHopperMod.COHO_MINECART_ENTITY_TYPE_ID);
            final EntityType<CopperHopperMinecartEntity> minecartType = EntityType.Builder.<CopperHopperMinecartEntity>of(CopperHopperMinecartEntity::new, MobCategory.MISC).
                sized(0.98f, 0.7f).build(minecartResourceKey);
            // ??? dimensions(EntityDimensions.fixed(0.98f, 0.7f)).build(); //??????
            final ResourceKey<Item> cartItemReourceKey = ResourceKey.create(Registries.ITEM, CopperHopperMod.COHO_MINECART_ITEM_ID);
            final CopperHopperMinecartItem cohoMinecartItem = new CopperHopperMinecartItem(new Item.Properties().stacksTo(1).setId(cartItemReourceKey));
            register(BuiltInRegistries.ENTITY_TYPE, COHO_MINECART_ENTITY_TYPE_ID, minecartType);
            register(BuiltInRegistries.ITEM, COHO_MINECART_ITEM_ID, cohoMinecartItem);
            ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> entries.addAfter(Items.HOPPER_MINECART, cohoMinecartItem));

            OxidizableBlocksRegistry.registerOxidizableBlockPair(BLOCK.getValue(COPPER_HOPPER), BLOCK.getValue(EXPOSED_COPPER_HOPPER));
            OxidizableBlocksRegistry.registerOxidizableBlockPair(BLOCK.getValue(EXPOSED_COPPER_HOPPER), BLOCK.getValue(WEATHERED_COPPER_HOPPER));
            OxidizableBlocksRegistry.registerOxidizableBlockPair(BLOCK.getValue(WEATHERED_COPPER_HOPPER), BLOCK.getValue(OXIDIZED_COPPER_HOPPER));

            OxidizableBlocksRegistry.registerWaxableBlockPair(BLOCK.getValue(COPPER_HOPPER), BLOCK.getValue(WAXED_COPPER_HOPPER));
            OxidizableBlocksRegistry.registerWaxableBlockPair(BLOCK.getValue(EXPOSED_COPPER_HOPPER), BLOCK.getValue(WAXED_EXPOSED_COPPER_HOPPER));
            OxidizableBlocksRegistry.registerWaxableBlockPair(BLOCK.getValue(WEATHERED_COPPER_HOPPER), BLOCK.getValue(WAXED_WEATHERED_COPPER_HOPPER));
            OxidizableBlocksRegistry.registerWaxableBlockPair(BLOCK.getValue(OXIDIZED_COPPER_HOPPER), BLOCK.getValue(WAXED_OXIDIZED_COPPER_HOPPER));
        }
    }

}

