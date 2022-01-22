package net.pcal.hopperfilter.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

// https://fabricmc.net/wiki/tutorial:mixin_injects
// https://fabricmc.net/wiki/tutorial:mixin_redirectors_methods

@SuppressWarnings("ALL")
@Mixin(HopperBlockEntity.class)
public abstract class HFHopperBlockEntityMixin {

    private static final Logger LOGGER = LogManager.getLogger(HFHopperBlockEntityMixin.class);

/**
    @Inject(method = "getAvailableSlots", at = @At("HEAD"), cancellable = true)
    private static void inject_getAvailableSlots(Inventory inventory, Direction side, CallbackInfoReturnable<IntStream> returnable) {
        LOGGER.info("[ItemFilter] getAvailableSlots? ");
        if (inventory instanceof HopperBlockEntity) {
            final HopperBlockEntity hopper = (HopperBlockEntity)inventory;
            if ("ItemFilter".equals(hopper.getCustomName())) {
                LOGGER.info("[ItemFilter] NONE!");
                //returnable.setReturnValue(IntStream.empty());
            }
        }
    }
    @Inject(method = "isInventoryEmpty", at = @At("HEAD"), cancellable = true)
    private static void inject_isInventoryEmpty(Inventory inventory, Direction side, CallbackInfoReturnable<Boolean> returnable) {
        LOGGER.info("[ItemFilter] isInventoryEmpty? ");
        returnable.setReturnValue(Boolean.TRUE);
    }

    @Inject(method = "isInventoryFull", at = @At("HEAD"), cancellable = true)
    private static void inject_isInventoryFull(Inventory inventory, Direction side, CallbackInfoReturnable<Boolean> returnable) {
        LOGGER.info("[ItemFilter] isInventoryFull? ");
        returnable.setReturnValue(Boolean.TRUE);
    }

    @Inject(method = "insertAndExtract(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/HopperBlockEntity;Ljava/util/function/BooleanSupplier;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void inject_insertAndExtract(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier, CallbackInfoReturnable<Boolean> returnable) {
        //LOGGER.info("[ItemFilter] insertAndExtract? ");
        //returnable.setReturnValue(Boolean.TRUE);
    }

    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
            at = @At("INVOKE"), cancellable = true)
    private static void inject_extract_no(World world, Hopper hopper, CallbackInfoReturnable<Boolean> returnable) {
    LOGGER.info("[ItemFilter] extract "+world+" "+hopper);
    if (hopper instanceof HopperBlockEntity) {
    LOGGER.info("[ItemFilter]  named "+((HopperBlockEntity)hopper).getName());
    }
        returnable.setReturnValue(Boolean.FALSE);
    }
**/

    private static boolean isFilterHopper(Inventory target) {
        if (target instanceof HopperBlockEntity) {
            final HopperBlockEntity hopperEntity = (HopperBlockEntity)target;
            final Text nameText = hopperEntity.getCustomName();
            return nameText != null && "ItemFilter".equals(nameText.asString());
        }
        return false;
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        if (!first.isOf(second.getItem())) {
            return false;
        }
        if (first.getDamage() != second.getDamage()) {
            return false;
        }
        if (first.getCount() > first.getMaxCount()) {
            return false;
        }
        return ItemStack.areNbtEqual(first, second);
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
              at = @At(value = "INVOKE", ordinal=0, target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack inject_getStack(Inventory inventory, int slot) {
        final ItemStack original = inventory.getStack(slot);
        if (isFilterHopper(inventory) && !containsMoreThan(inventory, original.getItem(), 1)) {
            //LOGGER.info("[ItemFilter] EMPTY!!! ");
            return ItemStack.EMPTY;
        } else {
            //LOGGER.info("[ItemFilter] not empty");
            return original;
        }
    }
/**
    @Inject(method = "insert(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/inventory/Inventory;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void __insert(World world, BlockPos pos, BlockState state, Inventory inventory) {
        Inventory inventory2 = HopperBlockEntity.getOutputInventory(world, pos, state);
        if (inventory2 == null) {
            return false;
        }
        Direction direction = state.get(HopperBlock.FACING).getOpposite();
        if (HopperBlockEntity.isInventoryFull(inventory2, direction)) {
            return false;
        }
        for (int i = 0; i < inventory.size(); ++i) {
            if (inventory.getStack(i).isEmpty()) continue;
            ItemStack itemStack = inventory.getStack(i).copy();
            ItemStack itemStack2 = HopperBlockEntity.transfer(inventory, inventory2, inventory.removeStack(i, 1), direction);
            if (itemStack2.isEmpty()) {
                inventory2.markDirty();
                return true;
            }
            inventory.setStack(i, itemStack);
        }
        return false;
    }
**/

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


        /**
        LOGGER.info("[ItemFilter] extract "+hopper+" "+inventory);
        ItemStack itemStack = inventory.getStack(slot);
        if (hopper instanceof HopperBlockEntity) {
         LOGGER.info("[ItemFilter] named "+((HopperBlockEntity)hopper).getName());
        }
        if (!hopper.isEmpty()) {
         LOGGER.info("[ItemFilter] BLOCKED !!!!!!!!!!!!!!!11");
         returnable.setReturnValue(Boolean.FALSE);
        } else {
            LOGGER.info("[ItemFilter] TAKE ONE !!!!!!!!!!!!!!!11");
        }
         **/

/**
    @Inject(method = "size()I", at = @At("RETURN"), cancellable = true)
    private void size_inject(CallbackInfoReturnable<Boolean> returnable) {
        final HopperBlockEntity hopperEntity = (HopperBlockEntity) (Object) this;
        final Text customNameText = hopperEntity.getCustomName();
        final String customName = customNameText == null ? null : customNameText.asString();
        LOGGER.info("[ItemFilter] size? "+customName);
    }
**/

    /**
     @Inject(method = "size()I", at = @At("RETURN"), cancellable = true)
     private void size_inject(CallbackInfoReturnable<Boolean> returnable) {
     final HopperBlockEntity hopperEntity = (HopperBlockEntity) (Object) this;
     final String customName = hopperEntity.getCustomName().asString();
     LOGGER.info("[ItemFilter] size? "+customName);
     }

    @Inject(method = "isFull", at = @At("RETURN"), cancellable = true)
    private void isFull_inject(CallbackInfoReturnable<Boolean> returnable) {
        final HopperBlockEntity hopperEntity = (HopperBlockEntity) (Object) this;
        final String customName = hopperEntity.getCustomName().asString();
        LOGGER.info("[ItemFilter] isFull? "+customName);
        if ("ItemFilter".equals(customName)) {
            LOGGER.info("[ItemFilter] YES");
            returnable.setReturnValue(true);
        }
    }
    **/
}
