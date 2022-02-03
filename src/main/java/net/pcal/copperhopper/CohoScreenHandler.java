package net.pcal.copperhopper;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class CohoScreenHandler extends HopperScreenHandler {

    public CohoScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(syncId, playerInventory);
    }

    public CohoScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return CohoService.getScreenHandlerType();
    }

}
