package net.pcal.copperhopper.polymer;

import eu.pb4.polymer.api.block.PolymerBlock;
import eu.pb4.polymer.api.client.PolymerKeepModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.pcal.copperhopper.CohoScreenHandler;
import net.pcal.copperhopper.CohoService;
import net.pcal.copperhopper.CopperHopperBlock;
import net.pcal.copperhopper.CopperHopperBlockEntity;

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
        return new LiteralText(CohoService.getInstance().getPolymerName());
    }

}
