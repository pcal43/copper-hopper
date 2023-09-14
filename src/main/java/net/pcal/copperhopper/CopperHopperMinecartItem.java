package net.pcal.copperhopper;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;

public class CopperHopperMinecartItem extends MinecartItem {

    public CopperHopperMinecartItem(Item.Settings settings) {
        super(Type.HOPPER, settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (!blockState.isIn(BlockTags.RAILS)) {
            return ActionResult.FAIL;
        } else {
            ItemStack itemStack = context.getStack();
            if (!world.isClient) {
                RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock ? (RailShape)blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                double d = 0.0;
                if (railShape.isAscending()) {
                    d = 0.5;
                }

                //FIXME find a better way to inject this
                final AbstractMinecartEntity abstractMinecartEntity = new CopperHopperMinecartEntity(world,
                       (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.0625 + d, (double)blockPos.getZ() + 0.5);

                if (itemStack.hasCustomName()) {
                    abstractMinecartEntity.setCustomName(itemStack.getName());
                }

                world.spawnEntity(abstractMinecartEntity);
                world.emitGameEvent(GameEvent.ENTITY_PLACE, blockPos, Emitter.of(context.getPlayer(), world.getBlockState(blockPos.down())));
            }

            itemStack.decrement(1);
            return ActionResult.success(world.isClient);
        }
    }
}
