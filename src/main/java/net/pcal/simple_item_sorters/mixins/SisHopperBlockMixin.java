package net.pcal.simple_item_sorters.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.pcal.simple_item_sorters.SisoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// https://fabricmc.net/wiki/tutorial:mixin_injects
// https://fabricmc.net/wiki/tutorial:mixin_redirectors_methods

@SuppressWarnings("ALL")
@Mixin(HopperBlock.class)
public abstract class SisHopperBlockMixin {

    final Logger logger = LogManager.getLogger(SisHopperBlockMixin.class);

    private static final BooleanProperty SORTER = BooleanProperty.of("sorter");

    @Inject(at = @At("TAIL"), method = " <init>()V")
    private void init(AbstractBlock.Settings settings, CallbackInfo cbi) {
        HopperBlock hp = ((HopperBlock)(Object)this);
        logger.info("!!!!!!!!!!!!!!!!!!!!!!!!! block "+hp);
    }

    @Inject(method = "onPlaced", at = @At("TAIL"), cancellable = false)
    private void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo cbi) {
        HopperBlockEntity blockEntity = (HopperBlockEntity) world.getBlockEntity(pos);
        final Text nameText = blockEntity.getCustomName();
        if (nameText != null && "Item Sorter".equals(nameText.asString())) {
            logger.info("PLACED AN ITEM SORTER !!!!!!!!!!!!!!!!!!!!!!!!! block ");
            world.setBlockState(pos, state.with(SORTER, true));
        } else {
            logger.info("PLACED A HOPPER");
            world.setBlockState(pos, state.with(SORTER, false));
        }
        BlockState bs = world.getBlockState(pos);
        logger.info("blockstate: "+bs);
    }

    @Inject(method = "appendProperties", at = @At("TAIL"), cancellable = false)
    private void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo cbi) {
        builder.add(SORTER);
    }
}
