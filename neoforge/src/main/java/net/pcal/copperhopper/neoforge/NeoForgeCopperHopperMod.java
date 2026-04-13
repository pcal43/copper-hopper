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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pcal.copperhopper.common.CohoScreenHandler;
import net.pcal.copperhopper.common.CopperHopperBlock;
import net.pcal.copperhopper.common.CopperHopperBlockEntity;
import net.pcal.copperhopper.common.CopperHopperItem;
import net.pcal.copperhopper.common.CopperHopperMinecartEntity;
import net.pcal.copperhopper.common.CopperHopperMinecartItem;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static net.pcal.copperhopper.common.CopperHopperMod.COHO_BLOCK_IDS;
import static net.pcal.copperhopper.common.CopperHopperMod.COHO_MINECART_ENTITY_TYPE_ID;
import static net.pcal.copperhopper.common.CopperHopperMod.COHO_MINECART_ITEM_ID;
import static net.pcal.copperhopper.common.CopperHopperMod.LOGGER_NAME;
import static net.pcal.copperhopper.common.CopperHopperMod.LOG_PREFIX;
import static net.pcal.copperhopper.common.CopperHopperMod.NS;
import static net.pcal.copperhopper.common.CopperHopperMod.mod;

@Mod("copperhopper")
public class NeoForgeCopperHopperMod {

    private static final Logger LOGGER = LogManager.getLogger(LOGGER_NAME);

    public NeoForgeCopperHopperMod(IEventBus modBus) {
        LOGGER.info(LOG_PREFIX + "Mod constructor called");
        modBus.addListener(NeoForgeCopperHopperMod::onCommonSetup);
        NeoForge.EVENT_BUS.addListener(NeoForgeCopperHopperMod::onServerStarting);
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

    private static void registerBlocksAndItems(IEventBus modBus) {
        // --- Menu / Screen ---
        DeferredRegister<MenuType<?>> menuTypes = DeferredRegister.create(BuiltInRegistries.MENU, NS);
        menuTypes.register("copper_hopper", () -> new MenuType<>(CohoScreenHandler::new, FeatureFlags.VANILLA_SET));
        menuTypes.register(modBus);

        // --- Blocks and Block Items ---
        DeferredRegister<net.minecraft.world.level.block.Block> blocks =
                DeferredRegister.create(BuiltInRegistries.BLOCK, NS);
        DeferredRegister<Item> items = DeferredRegister.create(BuiltInRegistries.ITEM, NS);

        final List<CopperHopperBlock> cohoBlocks = new ArrayList<>();
        for (final Tuple<Identifier, WeatherState> tuple : COHO_BLOCK_IDS) {
            final Identifier blockId = tuple.getA();
            final WeatherState weatherState = tuple.getB();
            final String path = blockId.getPath();
            final CopperHopperBlock cohoBlock = new CopperHopperBlock(weatherState, CopperHopperBlock.getDefaultSettings(blockId));
            cohoBlocks.add(cohoBlock);
            blocks.register(path, () -> cohoBlock);
            final ResourceKey<Item> itemResourceKey = ResourceKey.create(Registries.ITEM, blockId);
            final CopperHopperItem cohoItem = new CopperHopperItem(cohoBlock, new Item.Properties().setId(itemResourceKey).useBlockDescriptionPrefix());
            cohoItem.registerBlocks(Item.BY_BLOCK, cohoItem);
            items.register(path, () -> cohoItem);
        }

        // --- Block Entity ---
        DeferredRegister<BlockEntityType<?>> blockEntityTypes =
                DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NS);
        final CopperHopperBlock[] cohoBlockArray = cohoBlocks.toArray(new CopperHopperBlock[0]);
        blockEntityTypes.register("copper_hopper_entity",
                () -> new BlockEntityType<>(CopperHopperBlockEntity::new, cohoBlockArray));

        // --- Minecart Entity ---
        DeferredRegister<EntityType<?>> entityTypes =
                DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, NS);
        final ResourceKey<EntityType<?>> minecartResourceKey = ResourceKey.create(Registries.ENTITY_TYPE, COHO_MINECART_ENTITY_TYPE_ID);
        entityTypes.register("copper_hopper_minecart",
                () -> EntityType.Builder.<CopperHopperMinecartEntity>of(CopperHopperMinecartEntity::new, MobCategory.MISC)
                        .sized(0.98f, 0.7f)
                        .build(minecartResourceKey));

        // --- Minecart Item ---
        final ResourceKey<Item> cartItemResourceKey = ResourceKey.create(Registries.ITEM, COHO_MINECART_ITEM_ID);
        items.register("copper_hopper_minecart",
                () -> new CopperHopperMinecartItem(new Item.Properties().stacksTo(1).setId(cartItemResourceKey)));

        // Commit all deferred registers
        blocks.register(modBus);
        items.register(modBus);
        blockEntityTypes.register(modBus);
        entityTypes.register(modBus);
    }
}
