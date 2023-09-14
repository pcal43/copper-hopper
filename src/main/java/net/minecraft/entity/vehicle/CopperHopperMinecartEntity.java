package net.minecraft.entity.vehicle;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.Hopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.pcal.copperhopper.CohoScreenHandler;
import net.pcal.copperhopper.CohoService;

import java.lang.reflect.Field;

public class CopperHopperMinecartEntity extends StorageMinecartEntity implements Hopper, SidedInventory {

    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4};


    @Override
    Item getItem() {
        return CohoService.getItem();
    }


    public CopperHopperMinecartEntity(EntityType<? extends CopperHopperMinecartEntity> entityType, World world) {
        super(CohoService.getMinecartEntityType(), world);
    }

    public CopperHopperMinecartEntity(World world, double x, double y, double z) {
        super(CohoService.getMinecartEntityType(), x, y, z, world);
        Field value = null;
        try {
            value = Entity.class.getDeclaredField("type");
            value.setAccessible(true);
            value.set(this, CohoService.getMinecartEntityType());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
    public BlockState getDefaultContainedBlock() {
        return CohoService.getBlock().getDefaultState();
    }

    @Override
    public BlockState getContainedBlock() {
        return CohoService.getBlock().getDefaultState();
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

    public Type getMinecartType() {
        return Type.HOPPER;
    }


    public int size() {
        return 5;
    }

    @Override
    public double getHopperX() {
        return this.getX();
    }

    @Override
    public double getHopperY() {
        return this.getY() + 0.5;
    }

    @Override
    public double getHopperZ() {
        return this.getZ();
    }
}
