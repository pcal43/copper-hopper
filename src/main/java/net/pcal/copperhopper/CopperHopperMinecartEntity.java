package net.pcal.copperhopper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CopperHopperMinecartEntity extends HopperMinecartEntity implements SidedInventory {

    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4};

    public CopperHopperMinecartEntity(EntityType<? extends CopperHopperMinecartEntity> entityType, World world) {
        super(entityType, world);
    }

    public CopperHopperMinecartEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CohoScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("item.copperhopper.copper_hopper_minecart");
    }

    @Override
    public int[] getAvailableSlots(Direction ignored) {
        return SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return true;
        //return !CohoService.getInstance().shouldVetoPushInto(this, stack.getItem());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
        //return !CohoService.getInstance().shouldVetoPullFrom(this, stack.getItem());
    }
}
