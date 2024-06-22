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

package net.pcal.copperhopper;


import static net.pcal.copperhopper.CopperHopperMod.mod;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author pcal
 * @since 0.5.0
 */
public class CopperHopperMinecartEntity extends MinecartHopper implements WorldlyContainer, CopperInventory {

    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4};


    public CopperHopperMinecartEntity(EntityType<? extends CopperHopperMinecartEntity> entityType, Level world) {
        super(mod().getMinecartEntityType(), world);
    }

    public CopperHopperMinecartEntity(Level world, double x, double y, double z) {
        super(mod().getMinecartEntityType(), world);
        super.xo = x;
        super.yo = y;
        super.zo = z;
        super.setPos(x, y, z);
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new CohoScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("item.copperhopper.copper_hopper_minecart");
    }

    @Override
    public int[] getSlotsForFace(Direction ignored) {
        return SLOTS;
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return mod().getBlock().defaultBlockState();
    }

    @Override
    public BlockState getDisplayBlockState() {
        return mod().getBlock().defaultBlockState();
    }

    @Override
    protected Item getDropItem() {
        return mod().getMinecartItem();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return !mod().shouldVetoPushInto(this, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return !mod().shouldVetoPullFrom(this, stack);
    }


}

