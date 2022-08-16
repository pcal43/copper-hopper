package net.pcal.copperhopper.polymer;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.pcal.copperhopper.CohoService;
import net.pcal.copperhopper.CopperHopperBlockEntity;

public class PolymerCopperHopperBlockEntity extends CopperHopperBlockEntity {

    public PolymerCopperHopperBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new HopperScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Text getName() {
        return Text.literal(CohoService.getInstance().getPolymerName());
    }

    @Override
    public Text getDisplayName() {
        return Text.literal(CohoService.getInstance().getPolymerName());
    }

    @Override
    public Text getCustomName() {
        return Text.literal(CohoService.getInstance().getPolymerName());
    }
}
