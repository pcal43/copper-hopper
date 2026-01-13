/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 pcal.net
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.pcal.copperhopper;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.HashSet;
import java.util.Set;

import static net.pcal.copperhopper.CopperHopperMod.mod;

public class CopperHopperBlock extends HopperBlock implements WeatheringCopper {

    /**
     * Default block settings are shared used by both polymer and non-polymer registrations.
     */
    public static BlockBehaviour.Properties getDefaultSettings(final Identifier blockId) {
		final ResourceKey<Block> rk = ResourceKey.create(Registries.BLOCK, blockId);
        BlockBehaviour.Properties p = BlockBehaviour.Properties.ofFullCopy(Blocks.HOPPER).mapColor(MapColor.COLOR_BROWN).setId(rk);
        return p;
    }

    private final WeatherState weatherState;

    public CopperHopperBlock(WeatherState weatherState, BlockBehaviour.Properties settings) {
        super(settings);
        this.weatherState = weatherState;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : createTickerHelper(type, mod().getBlockEntityType(), HopperBlockEntity::pushItemsTick);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CopperHopperBlockEntity(pos, state);
    }

    /**
     * Override this to optionally exclude the 'filter items' when calculating redstone strength.
     */
    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, Direction direction) {
        if (CopperHopperMod.mod().isRedstoneStrengthIgnoresFilterItems()) {
            return getRedstoneSignalFromContainerExcludingFilterItems(level.getBlockEntity(blockPos));
        } else {
            return super.getAnalogOutputSignal(blockState, level, blockPos, direction);
        }
    }

    /**
     * Calculates the redstone strength the same way vanilla does, except that it excludes the first item
     * of a given type from the calculations (i.e., it ignores the 'filter items').
     */
    private static int getRedstoneSignalFromContainerExcludingFilterItems(@Nullable BlockEntity blockEntity) {
        if (blockEntity instanceof Container container) {
            float f = 0.0F;
            final Set<Item> itemTypes = new HashSet<>();
            for (int i = 0; i < container.getContainerSize(); ++i) {
                ItemStack itemStack = container.getItem(i);
                if (!itemStack.isEmpty()) {
                    final int discount;
                    if (!itemTypes.contains(itemStack.getItem())) {
                        // if this is the first stack of the given type of items, we're going to ignore one of them.
                        discount = 1;
                        itemTypes.add(itemStack.getItem());
                    } else {
                        discount = 0;
                    }
                    f += ((float) itemStack.getCount() - discount) / (float) (container.getMaxStackSize(itemStack) - discount);
                }
            }
            f /= (float) container.getContainerSize();
            return Mth.lerpDiscrete(f, 0, 15);
        } else {
            return 0;
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // This triggers the oxidation check
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        // Ensures the block actually receives ticks if a next stage exists
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }
}
