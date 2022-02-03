package net.pcal.copperhopper.polymer;

import eu.pb4.polymer.api.block.PolymerBlock;
import eu.pb4.polymer.api.client.PolymerKeepModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.pcal.copperhopper.CopperHopperBlock;

public class PolymerCopperHopperBlock extends CopperHopperBlock implements PolymerBlock, PolymerKeepModel {//, PolymerClientDecoded {

    public PolymerCopperHopperBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.HOPPER;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return this.getDefaultState()
                .with(HopperBlock.FACING, state.get(HopperBlock.FACING))
                .with(HopperBlock.ENABLED, state.get(HopperBlock.ENABLED));
    }

}
