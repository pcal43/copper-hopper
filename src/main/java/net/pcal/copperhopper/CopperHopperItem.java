package net.pcal.copperhopper;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class CopperHopperItem extends BlockItem {

    public CopperHopperItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    public Text getName() {
        return new LiteralText(CohoService.getInstance().getCopperHopperName());
    }

    public Text getName(ItemStack stack) {
        return this.getName();
    }
}
