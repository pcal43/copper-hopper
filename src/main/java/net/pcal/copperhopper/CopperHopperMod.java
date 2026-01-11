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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * Central singleton service.
 *
 * @author pcal
 * @since 0.0.1
 */
public class CopperHopperMod {

    // ===================================================================================
    // Constants

    public static final String LOGGER_NAME = "CopperHopper";
    public static final String LOG_PREFIX = "[CopperHopper] ";

    public static final Identifier COPPER_HOPPER = Identifier.parse("copperhopper:copper_hopper");
    public static final Identifier EXPOSED_COPPER_HOPPER = Identifier.parse("copperhopper:exposed_copper_hopper");
    public static final Identifier WEATHERED_COPPER_HOPPER = Identifier.parse("copperhopper:weathered_copper_hopper");
    public static final Identifier OXIDIZED_COPPER_HOPPER = Identifier.parse("copperhopper:oxidized_copper_hopper");
    public static final Identifier WAXED_COPPER_HOPPER = Identifier.parse("copperhopper:waxed_copper_hopper");
    public static final Identifier WAXED_EXPOSED_COPPER_HOPPER = Identifier.parse("copperhopper:waxed_exposed_copper_hopper");
    public static final Identifier WAXED_WEATHERED_COPPER_HOPPER = Identifier.parse("copperhopper:waxed_weathered_copper_hopper");
    public static final Identifier WAXED_OXIDIZED_COPPER_HOPPER = Identifier.parse("copperhopper:waxed_oxidized_copper_hopper");

    public static final java.util.List<Identifier> COHO_BLOCK_IDS = ImmutableList.of(
            COPPER_HOPPER,
            EXPOSED_COPPER_HOPPER,
            WEATHERED_COPPER_HOPPER,
            OXIDIZED_COPPER_HOPPER,
            WAXED_COPPER_HOPPER,
            WAXED_EXPOSED_COPPER_HOPPER,
            WAXED_WEATHERED_COPPER_HOPPER,
            WAXED_OXIDIZED_COPPER_HOPPER
    );

    public static final Identifier COHO_ITEM_ID = Identifier.parse("copperhopper:copper_hopper");


    public static final Identifier COHO_SCREEN_ID = Identifier.parse("copperhopper:copper_hopper");

    // I guess I shouldn't have added the '_entity' suffix here.  But it's out in the wild now, so too late to change.  *shrug*
    public static final Identifier COHO_BLOCK_ENTITY_TYPE_ID = Identifier.parse("copperhopper:copper_hopper_entity");

    public static final Identifier COHO_MINECART_ITEM_ID = Identifier.parse("copperhopper:copper_hopper_minecart");
    public static final Identifier COHO_MINECART_ENTITY_TYPE_ID = Identifier.parse("copperhopper:copper_hopper_minecart");


    private static final String CONFIG_FILENAME = "copperhopper.properties";
    private static final String DEFAULT_CONFIG_FILENAME = "default-copperhopper.properties";


    // ===================================================================================
    // Singleton

    private static final class SingletonHolder {
        private static final CopperHopperMod INSTANCE;
        static {
            INSTANCE = new CopperHopperMod();
        }
    }

    public static CopperHopperMod mod() {
        return SingletonHolder.INSTANCE;
    }

    // ===================================================================================
    // Mod-wide values

    public Item getMinecartItem() {
        return BuiltInRegistries.ITEM.getValue(COHO_MINECART_ITEM_ID);
    }

    public Block getMinecrartHopperBlock() {
        // COHO_BLOCK_IDS stores Identifier instances already — return the block by that Identifier.
        return BuiltInRegistries.BLOCK.getValue(COHO_BLOCK_IDS.get(0));
    }

    @SuppressWarnings("unchecked")
    public BlockEntityType<CopperHopperBlockEntity> getBlockEntityType() {
        return (BlockEntityType<CopperHopperBlockEntity>)
                requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(COHO_BLOCK_ENTITY_TYPE_ID));
    }

    @SuppressWarnings("unchecked")
    public EntityType<CopperHopperMinecartEntity> getMinecartEntityType() {
        return (EntityType<CopperHopperMinecartEntity>)
                requireNonNull(BuiltInRegistries.ENTITY_TYPE.getValue(COHO_MINECART_ENTITY_TYPE_ID));
    }

    @SuppressWarnings("unchecked")
    public MenuType<CohoScreenHandler> getScreenHandlerType() {
        return requireNonNull((MenuType<CohoScreenHandler>)
                BuiltInRegistries.MENU.getValue(COHO_SCREEN_ID));
    }

    // ===================================================================================
    // Fields

    private final Logger logger = LogManager.getLogger(LOGGER_NAME);
    private final Path configFilePath = Paths.get("config", CONFIG_FILENAME);
    private final File configFile = configFilePath.toFile();
    private Collection<Identifier> nbtMatchEnabledIds = Collections.emptySet();
    private boolean isRedstoneStrengthIgnoresFilterItems = false;

    // ===================================================================================
    // Mod lifecycle

    /**
     * Re/loads copperhopper.json.
     */
    public Properties loadConfig() throws IOException {
        final Properties config;
        setLogLevel(Level.INFO);
        try (final InputStream in = new FileInputStream(configFile)) {
            Properties newProps = new Properties();
            newProps.load(in);
            config = newProps;
        }

        final String nbtMatchEnabledIds = config.getProperty("nbtMatchEnabledIds");
        if (nbtMatchEnabledIds != null) {
            final ImmutableSet.Builder<Identifier> builder = ImmutableSet.builder();
            for (String id : nbtMatchEnabledIds.trim().split("\\s+")) {
                final Identifier r = Identifier.parse(id);
                logger.debug(() -> "nbtMatchEnabled for " + r);
                builder.add(r);
            }
            this.nbtMatchEnabledIds = builder.build();
        }

        this.isRedstoneStrengthIgnoresFilterItems = "true".equalsIgnoreCase(config.getProperty("redstoneStrengthIgnoresFilterItems", "false"));
        logger.debug(() -> "isRedstoneStrengthIgnoresFilterItems = "+this.isRedstoneStrengthIgnoresFilterItems);

        // adjust logging to configured level
        final String configuredLevel = config.getProperty("log-level");
        if (configuredLevel != null) {
            final Level logLevel = Level.getLevel(configuredLevel);
            if (logLevel == null) {
                logger.warn(LOG_PREFIX + "Invalid logLevel " + configuredLevel + " in " + configFile.getAbsolutePath());
            } else {
                setLogLevel(logLevel);
                logger.info(LOG_PREFIX + "LogLevel set to " + logLevel);
            }
        }
        logger.info(LOG_PREFIX + "Configuration loaded: " + config);
        return config;
    }

    /**
     *
     * Write a default configuration file if none exists.
     */
    public void createDefaultConfig() throws IOException {
        //
        // write out default config file if none exists
        //
        if (!configFile.exists()) {
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILENAME)) {
                if (in == null) {
                    throw new IllegalStateException("Unable to load " + DEFAULT_CONFIG_FILENAME);
                }
                configFilePath.getParent().toFile().mkdirs();
                java.nio.file.Files.copy(in, configFilePath);
                logger.info(LOG_PREFIX + "Wrote default " + CONFIG_FILENAME);
            }
        }
    }

    // ===================================================================================
    // Hopper behavior

    /**
     * Name to use in polymer mode.  FIXME i18n?
     */
    public String getPolymerName() {
        return "Item Filter";
    }

    /**
     * Return true if we should prevent one of the given Item from being pushed into the given copper hopper or
     * ch minecart.  THese should never accept item types they don't already contain.
     */
    public boolean shouldVetoPushInto(CopperInventory into, ItemStack pushedItem) {
        return !containsAtLeast(into, pushedItem, 1, this.nbtMatchEnabledIds);
    }

    /**
     * Return true if we should prevent one of the given Item from being pulled into the given inventory.
     * CopperHoppers should never pull item types they don't already contain.
     */
    public boolean shouldVetoPullInto(Container into, ItemStack pulledItem) {
        return isCopperHopper(into) && !containsAtLeast(into, pulledItem, 1, this.nbtMatchEnabledIds);
    }

    /**
     * Return true if we should prevent one of the given Item from being pulled from the given hopper.
     * CopperHoppers should never allow the last item of a given type to be pulled out.
     */
    public boolean shouldVetoPullFrom(CopperInventory from, ItemStack pulledItem) {
        return !containsAtLeast(from, pulledItem, 2, this.nbtMatchEnabledIds);
    }

    /**
     * Return true if we should exclude the filter items when calculating the strength of the redstone signal
     * output from a hopper.
     */
    public boolean isRedstoneStrengthIgnoresFilterItems() {
        return this.isRedstoneStrengthIgnoresFilterItems;
    }

    /**
     * Return true if we should prevent one of the given Item from being pulled from the given inventory.
     * CopperHoppers should never push their last item of a given type.
     */
    public boolean shouldVetoPushFrom(Container from, ItemStack pushedItem, net.minecraft.world.level.Level world, BlockPos pos) {
        if (!isCopperHopper(from)) return false;
        if (!containsAtLeast(from, pushedItem, 2, this.nbtMatchEnabledIds)) {
            return true; // never push the last one
        }
        // Check to see if the block below us is also a CopperHopper and if it's trying to filter on the
        // item we're about to push sideways.  If it is, hang onto to instead so the CopperHopper below
        // can pull it down instead.
        if (((CopperHopperBlockEntity)from).getBlockState().getValue(HopperBlock.FACING) == Direction.DOWN) {
            return false; // don't bother with the check if we're pointing down
        }
        final BlockPos below = pos.mutable().relative(Direction.Axis.Y, -1);
        final BlockEntity blockEntity = world.getBlockEntity(below);
        if (!isCopperHopper(blockEntity)) return false;
        return containsAtLeast((Container) blockEntity, pushedItem, 1, this.nbtMatchEnabledIds);
    }

    // ===================================================================================
    // Private

    /**
     * Returns true if the given inventory target is an Item Sorter hopper.
     */
    private static boolean isCopperHopper(Container target) {
        return target instanceof CopperInventory;
    }

    /**
     * Returns true if the given blockEntity is an Item Sorter hopper.
     */
    private static boolean isCopperHopper(BlockEntity blockEntity) {
        return blockEntity instanceof CopperHopperBlockEntity;
    }

    /**
     * Returns true if the given inventory contains at least the given number of the given item (across all slots).
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean containsAtLeast(Container inventory, ItemStack inputItem, int atLeast, Collection<Identifier> nbtMatchEnabledIds) {
        int count = 0;
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            final ItemStack filterStack = inventory.getItem(i);
            if (isMatch(filterStack, inputItem, nbtMatchEnabledIds)) {
                count += filterStack.getCount();
                if (count >= atLeast) return true; // don't bother counting the rest
            }
        }
        return false;
    }

    private static boolean isMatch(ItemStack first, ItemStack second, Collection<Identifier> nbtMatchEnabledIds) {
        if (second.isEmpty() || first.isEmpty()) return false;
        if (first == second) return true;
        return first.is(second.getItem()) &&
                (!nbtMatchEnabledIds.contains(BuiltInRegistries.ITEM.getKey(first.getItem())) ||
                        areNbtEqual(first, second));
    }

    private static boolean areNbtEqual(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        } else if (!left.isEmpty() && !right.isEmpty()) {
            final DataComponentPatch leftData = left.getComponentsPatch();
            final DataComponentPatch rightData = right.getComponentsPatch();
            return leftData != null && leftData.equals(rightData);
        } else {
            return false;
        }
    }

    /**
     * Manually adjust our logger's level.  Because changing the log4j config is a PITA.
     */
    private void setLogLevel(Level logLevel) {
        Configurator.setLevel(CopperHopperMod.class.getName(), logLevel);
    }
}
