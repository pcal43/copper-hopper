package net.pcal.mobfilter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class MFInitializer implements ModInitializer {

    private static final Logger LOGGER = LogManager.getLogger(MFInitializer.class);

    @Override
    public void onInitialize() {

        ClientLifecycleEvents.CLIENT_STARTED.register(server -> {
            try {
                MFService.getInstance().createDefaultConfig();
            } catch (Exception | NoClassDefFoundError e) {
                LOGGER.catching(Level.ERROR, e);
                LOGGER.error("[MobFilter] failed to initialize");
            }
        });

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            try {
                MFService.getInstance().loadConfig();
            } catch (Exception | NoClassDefFoundError e) {
                LOGGER.catching(Level.ERROR, e);
                LOGGER.error("[MobFilter] failed to initialize");
            }
        });
    }
}
