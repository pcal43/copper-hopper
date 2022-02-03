package net.pcal.copperhopper.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pcal.copperhopper.CohoService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// https://fabricmc.net/wiki/tutorial:mixin_injects
// https://fabricmc.net/wiki/tutorial:mixin_redirectors_methods

@SuppressWarnings("ALL")
@Mixin(HopperBlockEntity.class)
public abstract class CohoHopperBlockEntityMixin {

    @Redirect(method = "insert",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack __getStack(Inventory pushingInventory, int slot, World world, BlockPos pos, BlockState state, Inventory ignored) {
        final ItemStack original = pushingInventory.getStack(slot);
        if (CohoService.getInstance().shouldVetoPushFrom(pushingInventory, original.getItem())) {
            return ItemStack.EMPTY;
        }
        return original;

    }
/**
    //
    @Inject(method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void __extract_inventory(Hopper pullingHopper, Inventory pulledInventory, int slot, Direction side, CallbackInfoReturnable<Boolean> returnable) {
        if (CohoService.getInstance().shouldVetoPull(pullingHopper, pulledInventory, slot, side)) {
            returnable.setReturnValue(Boolean.FALSE);
        }
    }
**/
    @Inject(method = "extract(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/entity/ItemEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void __extract_itemEntity(Inventory pullingInventory, ItemEntity pulledEntity, CallbackInfoReturnable<Boolean> returnable) {
        if (CohoService.getInstance().shouldVetoPullInto(pullingInventory, pulledEntity.getStack().getItem())) {
            returnable.setReturnValue(Boolean.FALSE);
        }
    }
}
