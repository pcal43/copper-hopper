package net.pcal.simple_item_sorters.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pcal.simple_item_sorters.SisoService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopperBlock.class)
public abstract class SisHopperBlockMixin {

    @Inject(method = "onPlaced", at = @At("TAIL"), cancellable = false)
    private void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo cbi) {
        if (SisoService.getInstance().isItemSorter(itemStack)) {
            world.setBlockState(pos, state.with(SisoService.SORTER, 1));
        }
    }

    @Inject(method = "appendProperties", at = @At("TAIL"), cancellable = false)
    private void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo cbi) {
        builder.add(SisoService.SORTER);
    }
}
