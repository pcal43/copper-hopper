package net.pcal.simple_item_sorters.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopperBlock.class)
public abstract class SisHopperBlockMixin {

    private static final BooleanProperty SORTER = BooleanProperty.of("sorter");

    @Inject(method = "onPlaced", at = @At("TAIL"), cancellable = false)
    private void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo cbi) {
        final HopperBlockEntity blockEntity = (HopperBlockEntity) world.getBlockEntity(pos);
        final Text nameText = blockEntity.getCustomName();
        if (nameText != null && "Item Sorter".equals(nameText.asString())) {
            world.setBlockState(pos, state.with(SORTER, true));
        } else {
            world.setBlockState(pos, state.with(SORTER, false));
        }
    }

    @Inject(method = "appendProperties", at = @At("TAIL"), cancellable = false)
    private void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo cbi) {
        builder.add(SORTER);
    }
}
