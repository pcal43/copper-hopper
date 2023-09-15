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

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.pcal.copperhopper.CopperHopperBlock;

import static net.pcal.copperhopper.CopperHopperMod.mod;

public class PolymerCopperHopperBlock extends CopperHopperBlock implements PolymerBlock { // PolymerClientDecoded { //, PolymerKeepModel {//, PolymerClientDecoded {

    public PolymerCopperHopperBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.HOPPER;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.HOPPER.getDefaultState()
                .with(HopperBlock.FACING, state.get(HopperBlock.FACING))
                .with(HopperBlock.ENABLED, state.get(HopperBlock.ENABLED));
    }

    @Override
    public MutableText getName() {
        return Text.literal(mod().getPolymerName());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PolymerCopperHopperBlockEntity(pos, state);
    }


}
