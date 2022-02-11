package net.pcal.copperhopper;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CopperHopperBlock extends HopperBlock {

    /**
     * Default block settings are shared used by both polymer and non-polymer registrations.
     */
    public static AbstractBlock.Settings getDefaultSettings() {
        return FabricBlockSettings.copyOf(Blocks.HOPPER).mapColor(MapColor.BROWN);
    }

    public CopperHopperBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null :
                CopperHopperBlock.checkType(type, CohoService.getBlockEntityType(), HopperBlockEntity::serverTick);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CopperHopperBlockEntity(pos, state);
    }
}
