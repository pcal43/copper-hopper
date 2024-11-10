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

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.pcal.copperhopper.CopperHopperMinecartEntity;
import net.pcal.copperhopper.CopperHopperMinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.entity.EntitySpawnReason;

/**
 * Ensure CHMs get dispensed correctly.
 *
 * @author pcal
 * @since 0.5.0
 */
@Mixin(targets = "net/minecraft/world/item/MinecartItem$1") //
public abstract class MinecartItemDispenserBehaviorMixin { //KILL???
    @Redirect(method = "execute(Lnet/minecraft/core/dispenser/BlockSource;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;createMinecart(Lnet/minecraft/server/level/ServerLevel;DDDLnet/minecraft/world/entity/vehicle/AbstractMinecart$Type;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/entity/vehicle/AbstractMinecart;"))
    private AbstractMinecart coho__createMinecart(ServerLevel world, double x, double y, double z, EntityType<? extends AbstractMinecart> type, ItemStack stack0, Player player, BlockSource pointer, ItemStack stack) {
        if (stack.getItem() instanceof CopperHopperMinecartItem) {
            return new CopperHopperMinecartEntity(world, x, y, z);
        } else {
            return AbstractMinecart.createMinecart(world, x, y, z, type, EntitySpawnReason.DISPENSER, stack0, player);
        }
    }
}
