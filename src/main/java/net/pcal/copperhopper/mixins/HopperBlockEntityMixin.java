/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 pcal.net
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.pcal.copperhopper.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.pcal.copperhopper.CopperHopperMod.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("ALL")
@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    /**
     * Somewhat invasive change to prevent the hopper from pushing out its last item.  Basically make the stacks
     * read as empty if they shouldn't be pushed out.
     */
    @Redirect(method = "insert",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/Container;getItem(I)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack __getStack(Container pushingInventory, int slot, Level world, BlockPos pos, BlockState state, Container ignored) {
        final ItemStack original = pushingInventory.getItem(slot);
        if (mod().shouldVetoPushFrom(pushingInventory, original.getItem(), world, pos)) {
            return ItemStack.EMPTY;
        }
        return original;
    }

    /**
     * Apply filtering behavior to free floating entities above the hopper.
     */
    @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void __extract_itemEntity(Container pullingInventory, ItemEntity pulledEntity, CallbackInfoReturnable<Boolean> returnable) {
        if (mod().shouldVetoPullInto(pullingInventory, pulledEntity.getItem().getItem())) {
            returnable.setReturnValue(Boolean.FALSE);
        }
    }
}
