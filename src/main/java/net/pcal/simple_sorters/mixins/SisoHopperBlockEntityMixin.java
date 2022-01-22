package net.pcal.simple_sorters.mixins;

import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOGGER = LogManager.getLogger(SisoHopperBlockEntityMixin.class);

    private static boolean isFilterHopper(Inventory target) {
        if (target instanceof HopperBlockEntity) {
            final HopperBlockEntity hopperEntity = (HopperBlockEntity) target;
            final Text nameText = hopperEntity.getCustomName();
            return nameText != null && "ItemFilter".equals(nameText.asString());
        }
        return false;
    }

    private static boolean containsMoreThan(Inventory inventory, Item item, int moreThan) {
        int count = 0;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem().equals(item)) {
                count += itemStack.getCount();
                if (count > moreThan) return true;
            }
        }
        return false;
    }

    @Redirect(method = "insert",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack __getStack(Inventory inventory, int slot) {
        final ItemStack original = inventory.getStack(slot);
        if (isFilterHopper(inventory) && !containsMoreThan(inventory, original.getItem(), 1)) {
            //LOGGER.info("[ItemFilter] EMPTY!!! ");
            return ItemStack.EMPTY;
        } else {
            //LOGGER.info("[ItemFilter] not empty");
            return original;
        }
    }

    //
    @Inject(method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void __extract(Hopper pullingHopper, Inventory pulledInventory, int slot, Direction side, CallbackInfoReturnable<Boolean> returnable) {
        if (isFilterHopper(pulledInventory)) {
            final HopperBlockEntity pulledHopper = (HopperBlockEntity) pulledInventory;
            //LOGGER.info("[ItemFilter] extracting from "+String.valueOf(((HopperBlockEntity)pullingHopper).getName()));
            final Text nameText = pulledHopper.getCustomName();
            if (nameText != null && "ItemFilter".equals(nameText.asString())) {
                ItemStack itemStack = pulledInventory.getStack(slot);
                if (itemStack.getCount() <= 1 && itemStack.isStackable()) {
                    //LOGGER.info("[ItemFilter] BLOCKED ON MIN1 RULE !!!!!!!!!!!!!!!11");
                    returnable.setReturnValue(Boolean.FALSE);
                    return;
                }
            }
        }
        if (isFilterHopper(pullingHopper)) {
            ItemStack pulledStack = pulledInventory.getStack(slot);
            for (int i = 0; i < pullingHopper.size(); ++i) {
                if (containsMoreThan(pullingHopper, pulledStack.getItem(), 0)) return;
                //if (canMergeItems(pullingHopper.getStack(i), pulledStack)) return;
            }
            //LOGGER.info("[ItemFilter] BLOCKED ON MATCH RULE !!!!!!!!!!!!!!!11");
            returnable.setReturnValue(Boolean.FALSE);
            return;
        }
    }

}
