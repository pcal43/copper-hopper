package net.pcal.simple_item_sorters;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.pcal.simple_item_sorters.SisoService.LOG_PREFIX;

@SuppressWarnings("unused")
public class SisInitializer implements ModInitializer {

    @Override
    public void onInitialize() {

        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier("pcal:item_sorter"), new ItemSorterRecipe.ItemSorterRecipeSerializer());
//                Registry.register(Registry.RECIPE_SERIALIZER, ItemSorterRecipe.ItemSorterRecipeSerializer.ID, new ItemSorterRecipe.ItemSorterRecipeSerializer());
//                RecipeSerializer.register("pcal:item_sorter",  new ItemSorterRecipe.ItemSorterRecipeSerializer());
        //RecipeType<CraftingRecipe> SORTING = RecipeType.register("pcal:item_sorter");

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            final Logger logger = LogManager.getLogger(SisInitializer.class);
            try {
                SisoService.getInstance().loadConfig();


            } catch (Exception | NoClassDefFoundError e) {
                logger.catching(Level.ERROR, e);
                logger.error(LOG_PREFIX + "Failed to initialize");
            }
        });
    }
}
