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

package net.pcal.copperhopper.polymer;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.pcal.copperhopper.CopperHopperBlock;

import static net.pcal.copperhopper.CopperHopperMod.COHO_BLOCK_ENTITY_TYPE_ID;
import static net.pcal.copperhopper.CopperHopperMod.COHO_BLOCK_ID;
import static net.pcal.copperhopper.CopperHopperMod.COHO_ITEM_ID;

@SuppressWarnings("unused")
public class PolymerRegistrar implements Runnable {

    @Override
    public void run() {
        PolymerResourcePackUtils.addModAssets("copperhopper");
        final PolymerCopperHopperBlock cohoBlock = new PolymerCopperHopperBlock(CopperHopperBlock.getDefaultSettings(COHO_BLOCK_ID));
        final BlockEntityType<PolymerCopperHopperBlockEntity> cohoEntityType = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, COHO_BLOCK_ENTITY_TYPE_ID,
                FabricBlockEntityTypeBuilder.create(PolymerCopperHopperBlockEntity::new, cohoBlock).build());
        final PolymerCopperHopperItem cohoItem = new PolymerCopperHopperItem(cohoBlock, new Item.Properties());
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> entries.accept(cohoItem));

        cohoItem.registerBlocks(Item.BY_BLOCK, cohoItem); // wat
        Registry.register(BuiltInRegistries.ITEM, COHO_ITEM_ID, cohoItem);
        Registry.register(BuiltInRegistries.BLOCK, COHO_BLOCK_ID, cohoBlock);
        PolymerBlockUtils.registerBlockEntity(cohoEntityType);
    }
}
