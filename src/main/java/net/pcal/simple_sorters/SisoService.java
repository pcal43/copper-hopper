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

    private static final String CONFIG_FILENAME = "simple-sorters.properties";
    private static final String DEFAULT_CONFIG_FILENAME = "default-simple-sorters.properties";
    private static final String LOG_PREFIX = "[SimplerSorters]";
    private static final String DEFAULT_MAGIC_NAME = "Sorter";
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

    public ItemStack getStack(Inventory inventory, int slot) {
        final ItemStack original = inventory.getStack(slot);
        if (isFilterHopper(inventory) && !containsMoreThan(inventory, original.getItem(), 1)) {
            return ItemStack.EMPTY;
        } else {
            return original;
        }
    }

    public boolean isExtractingLastItem(Hopper pullingHopper, Inventory pulledInventory, int slot, Direction side) {
        if (isFilterHopper(pulledInventory)) {
            final HopperBlockEntity pulledHopper = (HopperBlockEntity) pulledInventory;
            this.logger.trace(() -> LOG_PREFIX + "extracting from " + pulledHopper);
            final Text nameText = pulledHopper.getCustomName();
            if (nameText != null && "ItemFilter".equals(nameText.asString())) {
                ItemStack itemStack = pulledInventory.getStack(slot);
                if (itemStack.getCount() <= 1 && itemStack.isStackable()) {
                    this.logger.trace(() -> LOG_PREFIX + " extraction blocked because it's the last one");
                    return true;
                }
            }
        }
        if (isFilterHopper(pullingHopper)) {
            ItemStack pulledStack = pulledInventory.getStack(slot);
            for (int i = 0; i < pullingHopper.size(); ++i) {
                if (containsMoreThan(pullingHopper, pulledStack.getItem(), 0)) return false;
                //if (canMergeItems(pullingHopper.getStack(i), pulledStack)) return;
            }
            this.logger.trace(() -> LOG_PREFIX + " extraction blocked because it doesn't match anything in the output");
        }
        return true;
    }

    // ===================================================================================
    // Private

    /**
     * Manually adjust our logger's level.  Because changing the log4j config is a PITA.
     */
    private void setLogLevel(Level logLevel) {
        Configurator.setLevel(SisoService.class.getName(), logLevel);
    }

    private boolean isFilterHopper(Inventory target) {
        if (target instanceof final HopperBlockEntity hopperEntity) {
            final Text nameText = hopperEntity.getCustomName();
            return nameText != null && this.magicName.equals(nameText.asString());
        }
        return false;
    }

    private static boolean containsMoreThan(Inventory inventory, Item item, int moreThan) {
        int count = 0;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem().equals(item)) {
                count += itemStack.getCount();
                if (count > moreThan) return true;
            }
        }
        return false;
    }
}
