package net.pcal.copperhopper.polymer;

import eu.pb4.polymer.api.block.PolymerBlockUtils;
import eu.pb4.polymer.api.item.PolymerBlockItem;
import eu.pb4.polymer.api.resourcepack.PolymerRPUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
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
        final BlockEntityType<PolymerCopperHopperBlockEntity> cohoEntityType = Registry.register(Registry.BLOCK_ENTITY_TYPE, COHO_BLOCK_ENTITY_TYPE_ID,
                FabricBlockEntityTypeBuilder.create(PolymerCopperHopperBlockEntity::new, cohoBlock).build(null));
        //final PolymerCopperHopperItem cohoItem = new PolymerCopperHopperItem(cohoBlock, new Item.Settings().group(ItemGroup.REDSTONE));
        final PolymerBlockItem cohoItem = new PolymerBlockItem(cohoBlock, new FabricItemSettings().group(ItemGroup.REDSTONE), Items.HOPPER);
        //new PolymerCopperHopperItem(cohoBlock, new Item.Settings().group(ItemGroup.REDSTONE));
        cohoItem.appendBlocks(Item.BLOCK_ITEMS, cohoItem); // wat
        PolymerBlockUtils.registerBlockEntity(cohoEntityType);
        Registry.register(Registry.ITEM, COHO_ITEM_ID, cohoItem);
        Registry.register(Registry.BLOCK, COHO_BLOCK_ID, cohoBlock);
    }
}
