package net.pcal.copperhopper.mixins;

import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.world.World;
import net.pcal.copperhopper.CopperHopperMinecartEntity;
import net.pcal.copperhopper.CopperHopperMinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Ensure CHMs get dispensed correctly.
 *
 * @author pcal
 * @since 0.5.0
 */
@Mixin(targets = "net/minecraft/item/MinecartItem$1")
public abstract class MinecartItemDispenserBehaviorMixin {

    @Redirect(method = "dispenseSilently(Lnet/minecraft/util/math/BlockPointer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;create(Lnet/minecraft/world/World;DDDLnet/minecraft/entity/vehicle/AbstractMinecartEntity$Type;)Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;"))
    private AbstractMinecartEntity coho__createMinecart(World world, double x, double y, double z, Type type, BlockPointer pointer, ItemStack stack) {
        if (stack.getItem() instanceof CopperHopperMinecartItem) {
            return new CopperHopperMinecartEntity(world, x, y, z);
        } else {
            return AbstractMinecartEntity.create(world, x, y, z, type);
        }
    }
}
