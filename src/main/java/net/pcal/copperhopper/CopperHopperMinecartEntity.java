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


import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static net.pcal.copperhopper.CopperHopperMod.mod;

/**
 * @author pcal
 * @since 0.5.0
 */
public class CopperHopperMinecartEntity extends HopperMinecartEntity implements SidedInventory, CopperInventory {

    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4};


    public CopperHopperMinecartEntity(EntityType<? extends CopperHopperMinecartEntity> entityType, World world) {
        super(mod().getMinecartEntityType(), world);
    }

    public CopperHopperMinecartEntity(World world, double x, double y, double z) {
        super(mod().getMinecartEntityType(), world);
        super.prevX = x;
        super.prevY = y;
        super.prevZ = z;
        super.setPosition(x, y, z);
    }

    @Override
    public ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CohoScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("item.copperhopper.copper_hopper_minecart");
    }

    @Override
    public int[] getAvailableSlots(Direction ignored) {
        return SLOTS;
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return mod().getBlock().getDefaultState();
    }

    @Override
    public BlockState getContainedBlock() {
        return mod().getBlock().getDefaultState();
    }

    @Override
    protected Item getItem() {
        return mod().getMinecartItem();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return !mod().shouldVetoPushInto(this, stack.getItem());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !mod().shouldVetoPullFrom(this, stack.getItem());
    }


}

