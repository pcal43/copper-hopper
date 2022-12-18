package net.pcal.copperhopper.polymer;

import eu.pb4.polymer.api.block.PolymerBlockUtils;
import eu.pb4.polymer.api.resourcepack.PolymerRPUtils;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.pcal.copperhopper.CopperHopperBlock;

import static net.pcal.copperhopper.CohoService.COHO_BLOCK_ENTITY_TYPE_ID;
import static net.pcal.copperhopper.CohoService.COHO_BLOCK_ID;
import static net.pcal.copperhopper.CohoService.COHO_ITEM_ID;

@SuppressWarnings("unused")
public class PolymerRegistrar implements Runnable {

    @Override
    public void run() {
        PolymerRPUtils.addAssetSource("copperhopper");
        final PolymerCopperHopperBlock cohoBlock = new PolymerCopperHopperBlock(CopperHopperBlock.getDefaultSettings());
        final BlockEntityType<PolymerCopperHopperBlockEntity> cohoEntityType = Registry.register(Registries.BLOCK_ENTITY_TYPE, COHO_BLOCK_ENTITY_TYPE_ID,
                FabricBlockEntityTypeBuilder.create(PolymerCopperHopperBlockEntity::new, cohoBlock).build());
        final PolymerCopperHopperItem cohoItem = new PolymerCopperHopperItem(cohoBlock, new Item.Settings());
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.add(cohoItem));

        cohoItem.appendBlocks(Item.BLOCK_ITEMS, cohoItem); // wat
        Registry.register(Registries.ITEM, COHO_ITEM_ID, cohoItem);
        Registry.register(Registries.BLOCK, COHO_BLOCK_ID, cohoBlock);
        PolymerBlockUtils.registerBlockEntity(cohoEntityType);
    }
}
