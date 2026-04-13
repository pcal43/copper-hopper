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

package net.pcal.copperhopper.neoforge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pcal.copperhopper.common.CohoScreenHandler;
import net.pcal.copperhopper.common.CohoBlock;
import net.pcal.copperhopper.common.CohoBlockEntity;
import net.pcal.copperhopper.common.CohoItem;
import net.pcal.copperhopper.common.CohoMinecartEntity;
import net.pcal.copperhopper.common.CohoMinecartItem;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static net.pcal.copperhopper.common.CohoMod.COHO_BLOCK_IDS;
import static net.pcal.copperhopper.common.CohoMod.COHO_MINECART_ENTITY_TYPE_ID;
import static net.pcal.copperhopper.common.CohoMod.COHO_MINECART_ITEM_ID;
import static net.pcal.copperhopper.common.CohoMod.LOGGER_NAME;
import static net.pcal.copperhopper.common.CohoMod.LOG_PREFIX;
import static net.pcal.copperhopper.common.CohoMod.NS;
import static net.pcal.copperhopper.common.CohoMod.mod;

@Mod("copperhopper")
public class NeoforgeMainInitializer {

    private static final Logger LOGGER = LogManager.getLogger(LOGGER_NAME);
    private static List<DeferredHolder<Item, CohoItem>> cohoItemHolders;

    public NeoforgeMainInitializer(IEventBus modBus) {
        LOGGER.info(LOG_PREFIX + "Mod constructor called");
        modBus.addListener(NeoforgeMainInitializer::onCommonSetup);
        modBus.addListener(NeoforgeMainInitializer::onBuildCreativeTab);
        NeoForge.EVENT_BUS.addListener(NeoforgeMainInitializer::onServerStarting);
        registerBlocksAndItems(modBus);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info(LOG_PREFIX + "FMLCommonSetupEvent fired");
        try {
            mod().createDefaultConfig();
        } catch (Exception e) {
            LOGGER.catching(Level.ERROR, e);
            LOGGER.error(LOG_PREFIX + "Failed to create default config");
        }
        // All registries are fully bound by FMLCommonSetupEvent — safe to call registerBlocks now
        for (DeferredHolder<Item, CohoItem> itemHolder : cohoItemHolders) {
            CohoItem item = itemHolder.get();
            item.registerBlocks(Item.BY_BLOCK, item);
        }
    }

    private static void onServerStarting(ServerStartingEvent event) {
        LOGGER.info(LOG_PREFIX + "ServerStartingEvent fired");
        try {
            mod().loadConfig();
        } catch (Exception e) {
            LOGGER.catching(Level.ERROR, e);
            LOGGER.error(LOG_PREFIX + "Failed to load config");
        }
    }

    private static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != CreativeModeTabs.REDSTONE_BLOCKS) return;
        // Add all copper hopper block items after the vanilla hopper
        for (DeferredHolder<Item, CohoItem> itemHolder : cohoItemHolders) {
            event.insertAfter(new ItemStack(Items.HOPPER), new ItemStack(itemHolder.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        // Add the minecart item after the vanilla hopper minecart
        Item minecartItem = BuiltInRegistries.ITEM.getValue(COHO_MINECART_ITEM_ID);
        event.insertAfter(new ItemStack(Items.HOPPER_MINECART), new ItemStack(minecartItem),
                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    private static void registerBlocksAndItems(IEventBus modBus) {
        // --- Menu / Screen ---
        DeferredRegister<MenuType<?>> menuTypes = DeferredRegister.create(Registries.MENU, NS);
        menuTypes.register("copper_hopper", () -> new MenuType<>(CohoScreenHandler::new, FeatureFlags.VANILLA_SET));
        menuTypes.register(modBus);

        // --- Blocks and Block Items ---
        DeferredRegister<net.minecraft.world.level.block.Block> blocks = DeferredRegister.create(Registries.BLOCK, NS);
        DeferredRegister<Item> items = DeferredRegister.create(Registries.ITEM, NS);

        final List<DeferredHolder<net.minecraft.world.level.block.Block, CohoBlock>> cohoBlockHolders = new ArrayList<>();
        cohoItemHolders = new ArrayList<>();

        for (final Tuple<Identifier, WeatherState> tuple : COHO_BLOCK_IDS) {
            final Identifier blockId = tuple.getA();
            final WeatherState weatherState = tuple.getB();
            final String path = blockId.getPath();

            final DeferredHolder<net.minecraft.world.level.block.Block, CohoBlock> blockHolder =
                    blocks.register(path, () -> new CohoBlock(weatherState, CohoBlock.getDefaultSettings(blockId)));
            cohoBlockHolders.add(blockHolder);

            final ResourceKey<Item> itemResourceKey = ResourceKey.create(Registries.ITEM, blockId);
            final DeferredHolder<Item, CohoItem> itemHolder =
                    items.register(path, () -> new CohoItem(blockHolder.get(), new Item.Properties().setId(itemResourceKey).useBlockDescriptionPrefix()));
            cohoItemHolders.add(itemHolder);
        }

        // --- Block Entity ---
        DeferredRegister<BlockEntityType<?>> blockEntityTypes = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NS);
        blockEntityTypes.register("copper_hopper_entity", () -> {
            CohoBlock[] cohoBlockArray = cohoBlockHolders.stream()
                    .map(DeferredHolder::get)
                    .toArray(CohoBlock[]::new);
            return new BlockEntityType<>(CohoBlockEntity::new, cohoBlockArray);
        });

        // --- Minecart Entity ---
        DeferredRegister<EntityType<?>> entityTypes = DeferredRegister.create(Registries.ENTITY_TYPE, NS);
        final ResourceKey<EntityType<?>> minecartResourceKey = ResourceKey.create(Registries.ENTITY_TYPE, COHO_MINECART_ENTITY_TYPE_ID);
        entityTypes.register("copper_hopper_minecart",
                () -> EntityType.Builder.<CohoMinecartEntity>of(CohoMinecartEntity::new, MobCategory.MISC)
                        .sized(0.98f, 0.7f)
                        .build(minecartResourceKey));

        // --- Minecart Item ---
        final ResourceKey<Item> cartItemResourceKey = ResourceKey.create(Registries.ITEM, COHO_MINECART_ITEM_ID);
        items.register("copper_hopper_minecart",
                () -> new CohoMinecartItem(new Item.Properties().stacksTo(1).setId(cartItemResourceKey)));

        // Commit all deferred registers to the mod event bus
        blocks.register(modBus);
        items.register(modBus);
        blockEntityTypes.register(modBus);
        entityTypes.register(modBus);
    }
}

