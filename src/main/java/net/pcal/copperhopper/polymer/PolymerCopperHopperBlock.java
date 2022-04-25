package net.pcal.copperhopper.polymer;

import eu.pb4.polymer.api.block.PolymerBlock;
import eu.pb4.polymer.api.client.PolymerClientDecoded;
import eu.pb4.polymer.api.client.PolymerKeepModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.pcal.copperhopper.CohoService;
import net.pcal.copperhopper.CopperHopperBlock;

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
        return new LiteralText(CohoService.getInstance().getPolymerName());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PolymerCopperHopperBlockEntity(pos, state);
    }


}
