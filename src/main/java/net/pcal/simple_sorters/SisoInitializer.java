package net.pcal.simple_sorters;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class SisoInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            final Logger logger = LogManager.getLogger(SisoInitializer.class);
            try {
                SisoService.getInstance().loadConfig();
                logger.info("[HopperFilter] Initialized");
            } catch (Exception | NoClassDefFoundError e) {
                logger.catching(Level.ERROR, e);
                logger.error("[HopperFilter] failed to initialize");
            }
        });
    }
}
