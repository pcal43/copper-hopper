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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.level.Level;
import net.pcal.copperhopper.CopperHopperMinecartEntity;
import net.pcal.copperhopper.CopperHopperMinecartItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;

/**
 * Ensure CHMs get placed correctly.
 *
 * @author pcal
 * @since 0.5.0
 */
@Mixin(MinecartItem.class)
public abstract class MinecartItemMixin {

    @Redirect(method = "useOn",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;createMinecart(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/EntitySpawnReason;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;"))
    private AbstractMinecart coho__createMinecart(Level world, double x, double y, double z, EntityType<? extends AbstractMinecart> type, EntitySpawnReason entitySpawnReason, ItemStack itemStack, @Nullable Player player) {
        if (((Object) this) instanceof CopperHopperMinecartItem) {
            return new CopperHopperMinecartEntity(world, x, y, z, (EntityType<? extends CopperHopperMinecartEntity>) type);
        } else {
            return AbstractMinecart.createMinecart(world, x, y, z, type, EntitySpawnReason.DISPENSER, itemStack, player);
        }
    }
}

