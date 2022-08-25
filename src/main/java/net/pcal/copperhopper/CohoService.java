package net.pcal.copperhopper;

import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
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
import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * Central singleton service.
 */
public class CohoService {

    // ===================================================================================
    // Constants

    public static final String LOGGER_NAME = "CopperHopper";
    public static final String LOG_PREFIX = "[CopperHopper] ";

    public static final Identifier COHO_BLOCK_ID = new Identifier("copperhopper:copper_hopper");
    public static final Identifier COHO_ITEM_ID = new Identifier("copperhopper:copper_hopper");
    public static final Identifier COHO_SCREEN_ID = new Identifier("copperhopper:copper_hopper");
    public static final Identifier COHO_BLOCK_ENTITY_TYPE_ID = new Identifier("copperhopper:copper_hopper_entity");

    private static final String CONFIG_FILENAME = "copperhopper.properties";
    private static final String DEFAULT_CONFIG_FILENAME = "default-copperhopper.properties";

    // ===================================================================================
    // Singleton

    private static final class SingletonHolder {
        private static final CohoService INSTANCE;
        static {
            INSTANCE = new CohoService();
        }
    }

    public static CohoService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // ===================================================================================
    // Fields

    private final Logger logger = LogManager.getLogger(LOGGER_NAME);
    private final Path configFilePath = Paths.get("config", CONFIG_FILENAME);
    private final File configFile = configFilePath.toFile();

    public static ScreenHandlerType<CohoScreenHandler> getScreenHandlerType() {
        //noinspection unchecked
        return requireNonNull((ScreenHandlerType<CohoScreenHandler>)
                Registry.SCREEN_HANDLER.get(COHO_SCREEN_ID));
    }

    public static  BlockEntityType<CopperHopperBlockEntity> getBlockEntityType() {
        //noinspection unchecked
        return (BlockEntityType<CopperHopperBlockEntity>)
                requireNonNull(Registry.BLOCK_ENTITY_TYPE.get(COHO_BLOCK_ENTITY_TYPE_ID));
    }

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
     * Return true if we should prevent one of the given Item from being pushed into the given hopper.
     * CopperHoppers should never accept item types they don't already contain.
     */
    public boolean shouldVetoPushInto(CopperHopperBlockEntity into, Item pushedItem) {
        return !containsAtLeast(into, pushedItem, 1);
    }

    /**
     * Return true if we should prevent one of the given Item from being pulled into the given inventory.
     * CopperHoppers should never pull item types they don't already contain.
     */
    public boolean shouldVetoPullInto(Inventory into, Item pulledItem) {
        return isCopperHopper(into) && !containsAtLeast(into, pulledItem, 1);
    }

    /**
     * Return true if we should prevent one of the given Item from being pulled from the given hopper.
     * CopperHoppers should never allow the last item of a given type to be pulled out.
     */
    public boolean shouldVetoPullFrom(CopperHopperBlockEntity from, Item pulledItem) {
        return !containsAtLeast(from, pulledItem, 2);
    }

    /**
     * Return true if we should prevent one of the given Item from being pulled from the given inventory.
     * CopperHoppers should never push their last item of a given type.
     */
    public boolean shouldVetoPushFrom(Inventory from, Item pushedItem, World world, BlockPos pos) {
        if (!isCopperHopper(from)) return false;
        if (!containsAtLeast(from, pushedItem, 2)) {
            return true; // never push the last one
        }
        // Check to see if the block below us is also a CopperHopper and if it's trying to filter on the
        // item we're about to push sideways.  If it is, hang onto to instead so the CopperHopper below
        // can pull it down instead.
        if (((CopperHopperBlockEntity)from).getCachedState().get(HopperBlock.FACING) == Direction.DOWN) {
            return false; // don't bother with the check if we're pointing down
        }
        final BlockPos below = pos.mutableCopy().offset(Direction.Axis.Y, -1);
        final BlockEntity blockEntity = world.getBlockEntity(below);
        if (!isCopperHopper(blockEntity)) return false;
        return containsAtLeast((Inventory) blockEntity, pushedItem, 1);
    }

    // ===================================================================================
    // Private

    /**
     * Returns true if the given inventory target is an Item Sorter hopper.
     */
    private static boolean isCopperHopper(Inventory target) {
        return target instanceof CopperHopperBlockEntity;
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
    private static boolean containsAtLeast(Inventory inventory, Item item, int atLeast) {
        int count = 0;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem().equals(item)) {
                count += itemStack.getCount();
                if (count >= atLeast) return true; // don't bother counting the rest
            }
        }
        return false;
    }

    /**
     * Manually adjust our logger's level.  Because changing the log4j config is a PITA.
     */
    private void setLogLevel(Level logLevel) {
        Configurator.setLevel(CohoService.class.getName(), logLevel);
    }
}
