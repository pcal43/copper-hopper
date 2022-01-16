package net.pcal.mobfilter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobFilterInitializer implements ModInitializer {

    private static final Logger LOGGER = LogManager.getLogger(MobFilterInitializer.class);

	/**
    private static final Map<EntityType<?>, FabricEntityObj> entities = new WeakHashMap<>();
    private static final Map<WorldAccess, FabricWorldObj> worlds = new WeakHashMap<>();

    public static FabricEntityObj wrapEntity(EntityType<?> entity) {
        if (!entities.containsKey(entity))
            entities.put(entity, new FabricEntityObj(entity));
        return entities.get(entity);
    }

    public static FabricWorldObj wrapWorld(ServerWorld world) {
        if (!worlds.containsKey(world))
            worlds.put(world, new FabricWorldObj(world));
        return worlds.get(world);
    }
	**/
	
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            MobFilterService.getInstance().loadConfig();
	    //            registerCommands(server);
        });
	/**
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
            if (success)
                registerCommands(server);
        });
	**/
        //reloadConfig();
    }

    /**
    public void registerCommands(MinecraftServer server) {
        server.getCommandManager().getDispatcher().register(CommandManager.literal("reloadpeace").requires(source -> {
            return source.hasPermissionLevel(3);
        }).executes(context -> {
            server.execute(() -> {
                reloadConfig();
                context.getSource().sendFeedback(new TranslatableText("commands.reloadpeace.done", new Object[0]).setStyle(Style.EMPTY.withColor(Formatting.YELLOW)), true);
            });
            return 0;
        }));
    }
    **/

    /**
    public void reloadConfig() {
        try {
            LOGGER.info("[PeacefulSurface] Loading filters...");
            PeaceAPI.clearFilters();
            File dir = Paths.get(".", "config", "PeacefulSurface_Rules").toFile();

            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    try {
                        LOGGER.info("[PeacefulSurface] Writing DefaultRule...");
                        FileUtils.copyInputStreamToFile(FabricPeacefulSurface.class.getResourceAsStream("/DefaultRule.json"), new File(dir, "DefaultRule.json"));
                        LOGGER.info("[PeacefulSurface] Successfully wrote DefaultRule.");
                    } catch (IOException e) {
                        LOGGER.error("[PeacefulSurface] Failed to write DefaultRule.", e);
                    }
                }
            }

            JsonRule.fromDirectory(dir).forEach(PeaceAPI::addFilter);
            PeaceAPI.notifyReloadListeners();
            LOGGER.info("[PeacefulSurface] Loaded {} filters.", PeaceAPI.countFilters());
        } catch (Exception e) {
            LOGGER.error("[PeacefulSurface] Failed to load filters.", e);
            PeaceAPI.clearFilters();
        }
    }
     **/

}
