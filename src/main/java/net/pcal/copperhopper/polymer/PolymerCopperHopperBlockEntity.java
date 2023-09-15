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

package net.pcal.copperhopper.polymer;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.pcal.copperhopper.CopperHopperBlockEntity;

import static net.pcal.copperhopper.CopperHopperMod.mod;

public class PolymerCopperHopperBlockEntity extends CopperHopperBlockEntity  {

    public PolymerCopperHopperBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new HopperScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Text getName() {
        return Text.literal(mod().getPolymerName());
    }

    @Override
    public Text getDisplayName() {
        return Text.literal(mod().getPolymerName());
    }

    @Override
    public Text getCustomName() {
        return Text.literal(mod().getPolymerName());
    }
}
