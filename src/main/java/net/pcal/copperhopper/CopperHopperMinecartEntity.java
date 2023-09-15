package net.pcal.copperhopper;


import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * @author pcal
 * @since 0.5.0
 */
public class CopperHopperMinecartEntity extends HopperMinecartEntity implements SidedInventory, CopperInventory {

    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4};


    public CopperHopperMinecartEntity(EntityType<? extends CopperHopperMinecartEntity> entityType, World world) {
        super(CohoService.getMinecartEntityType(), world);
    }

    public CopperHopperMinecartEntity(World world, double x, double y, double z) {
        super(CohoService.getMinecartEntityType(), world);
        super.prevX = x;
        super.prevY = y;
        super.prevZ = z;
        super.setPosition(x, y, z);
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
    protected Item getItem() {
        return CohoService.getMinecartItem();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return !CohoService.getInstance().shouldVetoPushInto(this, stack.getItem());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !CohoService.getInstance().shouldVetoPullFrom(this, stack.getItem());
    }


}

