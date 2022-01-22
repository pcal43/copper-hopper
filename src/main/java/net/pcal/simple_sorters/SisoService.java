package net.pcal.simple_sorters;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

/**
 * Central singleton service.
 */
public class SisoService {

    // ===================================================================================
    // Constants

    static final String LOG_PREFIX = "[SimpleSorters] ";
    private static final String CONFIG_FILENAME = "simple-sorters.properties";
    private static final String DEFAULT_CONFIG_FILENAME = "default-simple-sorters.properties";
    private static final String DEFAULT_MAGIC_NAME = "Item Sorter";
    private static final boolean DEFAULT_SORT_STACKABLES = true;
    private static final boolean DEFAULT_SORT_UNSTACKABLES = false;

    // ===================================================================================
    // Singleton

    private static final class SingletonHolder {
        private static final SisoService INSTANCE;

        static {
            INSTANCE = new SisoService();
        }
    }

    public static SisoService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // ===================================================================================
    // Fields

    private final Logger logger = LogManager.getLogger(SisoService.class);
    private final Path configFilePath = Paths.get("config", CONFIG_FILENAME);
    private final File configFile = configFilePath.toFile();
    private boolean sortStackables = DEFAULT_SORT_STACKABLES;
    private boolean sortUnstackables = DEFAULT_SORT_UNSTACKABLES;
    private String magicName = DEFAULT_MAGIC_NAME;

    // ===================================================================================
    // Mod lifecycle

    /**
     * /**
     * Write a default configuration file if none exists.
     */
    public void createDefaultConfig() {
        //
        // write out default config file if none exists
        //
        if (!configFile.exists()) {
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILENAME)) {
                if (in == null) {
                    throw new IllegalStateException("Unable to load " + DEFAULT_CONFIG_FILENAME);
                }
                java.nio.file.Files.copy(in, configFilePath);
                logger.info(LOG_PREFIX + "Wrote default " + CONFIG_FILENAME);
            } catch (Exception e) {
                logger.catching(Level.ERROR, e);
                logger.error(LOG_PREFIX + "Failed to write default configuration file to " + configFile.getAbsolutePath());
            }
        }
    }

    /**
     * Re/loads mobfilter.yaml and initializes a new FiluterRuleList.
     */
    public void loadConfig() {
        createDefaultConfig();
        try {
            setLogLevel(Level.INFO);
            // load the config file and build the rules
            Properties config;
            try (final InputStream in = new FileInputStream(configFile)) {
                Properties newProps = new Properties();
                newProps.load(in);
                config = newProps;
            }
            // load settings
            this.magicName = config.getProperty("sorting-hopper-name", DEFAULT_MAGIC_NAME);
            // err on the side of assuming it's on
            this.sortStackables = !"false".equals(config.getProperty("sort-stackables", String.valueOf(DEFAULT_SORT_STACKABLES)));
            this.sortUnstackables = !"false".equals(config.getProperty("sort-unstackables", String.valueOf(DEFAULT_SORT_UNSTACKABLES)));

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
            logger.info(LOG_PREFIX + "configuration loaded: " + config);
        } catch (Exception e) {
            logger.catching(Level.ERROR, e);
            logger.error(LOG_PREFIX + "Failed to load configuration from " + configFile.getAbsolutePath());
        }
    }

    // ===================================================================================
    // Hopper behavior

    /**
     * Returns true if we should prevent the pushingInventory from pushing (inserting) an
     * item from the given stack.
     */
    public boolean shouldVetoPush(Inventory pushingInventory, ItemStack stackToPush) {
        this.logger.trace(() -> (LOG_PREFIX + " shouldVetoPush "+pushingInventory+" "+stackToPush));
        // If we're pushing into an Item Sorter, make sure we don't push a block of a type that isn't already there
        return isItemSorter(pushingInventory) && containsLessThan(pushingInventory, stackToPush.getItem(), 2);
    }

    /**
     * Returns true if we should prevent the pullingInventory from pushing (extracting) an
     * item from the given slot of the sourceInventory.
     */
    public boolean shouldVetoPull(Hopper pullingHopper, Inventory sourceInventory, int slot, Direction side) {
        this.logger.trace(() -> (LOG_PREFIX + " shouldVetoPull "+pullingHopper+" "+sourceInventory+" "+slot));
        final ItemStack pulledStack = sourceInventory.getStack(slot);
        if (isItemSorter(sourceInventory) && isSortableStack(pulledStack)) {
            // If we're pulling from an Item Sorter, make sure we don't pull the last block of its type
            if (containsLessThan(sourceInventory, pulledStack.getItem(), 2)) {
                this.logger.trace(() -> (LOG_PREFIX + " vetoing pull of "+pulledStack+" from "+sourceInventory));
                return true;
            }
        }
        if (isItemSorter(pullingHopper)) {
            // If we're pulling into an Item Sorter, make sure we don't pull a block of a type that isn't already there
            if (containsLessThan(pullingHopper, pulledStack.getItem(), 1)) {
                this.logger.trace(() -> (LOG_PREFIX + " extraction blocked because it doesn't match anything in the output"));
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    // Private

    /**
     * Returns true if modified hopper behavior should apply to the given stack.
     */
    private boolean isSortableStack(ItemStack item) {
        return item.isStackable() ? this.sortStackables : this.sortUnstackables;
    }

    /**
     * Returns true if the given inventory target is an Item Sorter hopper.
     */
    private boolean isItemSorter(Inventory target) {
        if (target instanceof final HopperBlockEntity hopperEntity) {
            final Text nameText = hopperEntity.getCustomName();
            return nameText != null && this.magicName.equals(nameText.asString());
        }
        return false;
    }

    /**
     * Returns true if the given inventory contains more than the given number of the given item (across all slots).
     */
    private static boolean containsLessThan(Inventory inventory, Item item, int lessThan) {
        int count = 0;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem().equals(item)) {
                count += itemStack.getCount();
                if (count >= lessThan) return false;
            }
        }
        return true;
    }

    /**
     * Manually adjust our logger's level.  Because changing the log4j config is a PITA.
     */
    private void setLogLevel(Level logLevel) {
        Configurator.setLevel(SisoService.class.getName(), logLevel);
    }
}
