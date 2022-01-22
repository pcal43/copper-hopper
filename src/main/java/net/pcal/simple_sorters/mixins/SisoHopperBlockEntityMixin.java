package net.pcal.simple_sorters.mixins;

import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.pcal.simple_sorters.SisoService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// https://fabricmc.net/wiki/tutorial:mixin_injects
// https://fabricmc.net/wiki/tutorial:mixin_redirectors_methods

@SuppressWarnings("ALL")
@Mixin(HopperBlockEntity.class)
public abstract class SisoHopperBlockEntityMixin {

    @Redirect(method = "insert",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack __getStack(Inventory inventory, int slot) {
        final ItemStack original = inventory.getStack(slot);
        if (SisoService.getInstance().shouldVetoPush(inventory, original)) {
            return ItemStack.EMPTY;
        } else {
            return original;
        }
    }

    //
    @Inject(method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void __extract(Hopper pullingHopper, Inventory pulledInventory, int slot, Direction side, CallbackInfoReturnable<Boolean> returnable) {
        if (SisoService.getInstance().shouldVetoPull(pullingHopper, pulledInventory, slot, side)) {
            returnable.setReturnValue(Boolean.FALSE);
        }
    }
}
