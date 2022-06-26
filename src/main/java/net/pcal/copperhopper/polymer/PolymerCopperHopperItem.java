package net.pcal.copperhopper.polymer;

import eu.pb4.polymer.api.item.PolymerItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.pcal.copperhopper.CohoService;
import net.pcal.copperhopper.CopperHopperItem;
import org.jetbrains.annotations.Nullable;

public class PolymerCopperHopperItem extends CopperHopperItem implements PolymerItem {//}, PolymerKeepModel, PolymerClientDecoded {

    public PolymerCopperHopperItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.HOPPER;
    }

    @Override
    public Text getName() {
        return Text.literal(CohoService.getInstance().getPolymerName());
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.literal(CohoService.getInstance().getPolymerName());
    }
}
