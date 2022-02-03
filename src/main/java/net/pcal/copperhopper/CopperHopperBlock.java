package net.pcal.copperhopper;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CopperHopperBlock extends HopperBlock {

    public static AbstractBlock.Settings getDefaultSettings() {
        return AbstractBlock.Settings.of(Material.METAL, MapColor.BROWN).requiresTool().strength(3.0f, 4.8f).
                sounds(BlockSoundGroup.METAL).nonOpaque();
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

    @Override
    public MutableText getName() {
        return new LiteralText(CohoService.getInstance().getCopperHopperName());
    }
}
