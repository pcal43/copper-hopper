package net.pcal.copperhopper.polymer;

import eu.pb4.polymer.api.client.PolymerClientDecoded;
import eu.pb4.polymer.api.client.PolymerKeepModel;
import eu.pb4.polymer.api.item.PolymerItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.pcal.copperhopper.CohoService;
import net.pcal.copperhopper.CopperHopperItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        return new LiteralText(CohoService.getInstance().getPolymerName());
    }

    @Override
    public void modifyClientTooltip(List<Text> tooltip, ItemStack stack, ServerPlayerEntity player) {
        tooltip.clear();
        tooltip.add(getName());
    }
}
