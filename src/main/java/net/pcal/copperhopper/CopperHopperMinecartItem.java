package net.pcal.copperhopper;

import net.minecraft.entity.vehicle.AbstractMinecartEntity.Type;
import net.minecraft.item.Item;
import net.minecraft.item.MinecartItem;

/**
 * @author pcal
 * @since 0.5.0
 */
public class CopperHopperMinecartItem extends MinecartItem {

    public CopperHopperMinecartItem(Item.Settings settings) {
        super(Type.HOPPER, settings);
    }
}
