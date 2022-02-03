package net.pcal.copperhopper;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
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
    private String copperHopperName = "Item Filter"; // FIXME i18n

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
     * Re/loads mobfilter.yaml and initializes a new FiluterRuleList.
     */
    public Properties loadConfig() throws IOException {
        final Properties config;
        setLogLevel(Level.INFO);
        try (final InputStream in = new FileInputStream(configFile)) {
            Properties newProps = new Properties();
            newProps.load(in);
            config = newProps;
        }

        if (config.containsKey("copper-hopper-name")) {
            this.copperHopperName = config.getProperty("copper-hopper-name");
        }

        // adjust logging to configured level
        final String configuredLevel = config.getProperty("log-level");
        if (configuredLevel != null) {
            final Level logLevel = Level.getLevel(configuredLevel);
            if (logLevel == null) {
                logger.warn("Invalid logLevel " + configuredLevel + " in " + configFile.getAbsolutePath());
            } else {
                setLogLevel(logLevel);
                logger.info("LogLevel set to " + logLevel);
            }
        }
        logger.info("Configuration loaded: " + config);
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
                java.nio.file.Files.copy(in, configFilePath);
                logger.info("Wrote default " + CONFIG_FILENAME);
            }
        }
    }

    // ===================================================================================
    // Hopper behavior

    /**
     * Return true if we should prevent one of the given Item from being pushed into the given hopper.
     * CopperHoppers should never accept item types they don't already contain.
     */
    public boolean shouldVetoPushInfo(CopperHopperBlockEntity into, Item pushedItem) {
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
    public boolean shouldVetoPushFrom(Inventory from, Item pushedItem) {
        return isCopperHopper(from) && !containsAtLeast(from, pushedItem, 2);
    }

    public String getCopperHopperName() {
        return copperHopperName;
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
