package net.pcal.copperhopper.mixins;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity.Type;
import net.minecraft.item.MinecartItem;
import net.minecraft.world.World;
import net.pcal.copperhopper.CopperHopperMinecartEntity;
import net.pcal.copperhopper.CopperHopperMinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Ensure CHMs get placed correctly.
 *
 * @author pcal
 * @since 0.5.0
 */
@Mixin(MinecartItem.class)
public abstract class MinecartItemMixin {

    //FIXME repeat for dispenseSilently

    @Redirect(method = "useOnBlock",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;create(Lnet/minecraft/world/World;DDDLnet/minecraft/entity/vehicle/AbstractMinecartEntity$Type;)Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;"))
    private AbstractMinecartEntity coho__createMinecart(World world, double x, double y, double z, Type type) {
        if (((Object)this) instanceof CopperHopperMinecartItem) {
            return new CopperHopperMinecartEntity(world, x, y, z);
        } else {
            return AbstractMinecartEntity.create(world, x, y, z, type);
        }
    }

}




