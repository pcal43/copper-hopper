package net.pcal.simple_item_sorters;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class ItemSorterRecipe extends ShapedRecipe  {

//    RecipeType<ItemSorterRecipe> TYPE = RecipeType.register("pcal:item_sorter");

    public ItemSorterRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> ingredients, ItemStack output) {
        super(id, group, width, height, ingredients, SisoService.getInstance().setItemSorter(output));
    }

    /**
    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }**/

    /**
    public static class Type implements RecipeType<ItemSorterRecipe> {
        // Define ExampleRecipe.Type as a singleton by making its constructor private and exposing an instance.
        private Type() {}
        public static final Type INSTANCE = new Type();

        // This will be the "type" field in the json
        public static final Identifier ID = new Identifier("pcal:item_sorter");
    }**/

    public static class ItemSorterRecipeSerializer implements RecipeSerializer<ItemSorterRecipe> {

        private final Serializer delegate;

        // Define ExampleRecipeSerializer as a singleton by making its constructor private and exposing an instance.
        public ItemSorterRecipeSerializer() {
            this.delegate = new ShapedRecipe.Serializer();
        }

        //public static final ItemSorterRecipeSerializer INSTANCE = new ItemSorterRecipeSerializer();

        // This will be the "type" field in the json
        //public static final Identifier ID = new Identifier("pcal:item_sorter");

        @Override
        public ItemSorterRecipe read(Identifier id, JsonObject json) {
            final ShapedRecipe r = this.delegate.read(id, json);
            return new ItemSorterRecipe(r.getId(), r.getGroup(), r.getWidth(), r.getHeight(), r.getIngredients(), r.getOutput());
        }

        @Override
        public ItemSorterRecipe read(Identifier id, PacketByteBuf buf) {
            final ShapedRecipe r = this.delegate.read(id, buf);
            return new ItemSorterRecipe(r.getId(), r.getGroup(), r.getWidth(), r.getHeight(), r.getIngredients(), r.getOutput());
        }

        @Override
        public void write(PacketByteBuf buf, ItemSorterRecipe recipe) {
            this.delegate.write(buf, recipe);
        }
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
//        if (FabricLoader.getInstance().isModLoaded("nbtcrafting")) {
//            return NbtCraftingUtil.getOutputStack(getOutput(), getPreviewInputs(), inv);
//        }
        ItemStack i = this.getOutput().copy();
        SisoService.getInstance().setItemSorter(i);
        return i;
    }

}
