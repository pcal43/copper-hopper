package net.pcal.simple_item_sorters;


import net.minecraft.block.Material;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import com.google.gson.JsonObject;

public class SisRecipeType extends ShapedRecipe {

    public SisRecipeType(Identifier id, String group, int width, int height, DefaultedList<Ingredient> input, ItemStack output) {

    }

//    public SisRecipeType(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
//        super(TEST_RECIPE_TYPE, id, group, input, output, experience, cookTime);
//    }

//    @Override
//    public ItemStack getRecipeKindIcon() {
//        return new ItemStack(Items.BLACKSTONE);
//    }

//    @Override
//    public RecipeSerializer<?> getSerializer() {
//        return TEST_RECIPE_SERIALIZER;
//    }

    public static final String MOD_ID = "simple-item-sorters";
    public static final String MOD_NAME = "simple-item-sorters";

/**    public static final Block TEST_FURNACE_BLOCK;
    public static final BlockEntityType TEST_FURNACE_BLOCK_ENTITY;
**/
    public static final RecipeType<SisRecipeType> TEST_RECIPE_TYPE;

   // public static final RecipeSerializer<TestRecipe> TEST_RECIPE_SERIALIZER;

    //public static final ScreenHandlerType<TestFurnaceScreenHandler> TEST_FURNACE_SCREEN_HANDLER;

    static {
        //TEST_FURNACE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "test_furnace"), new TestFurnace(FabricBlockSettings.of(Material.METAL)));
        //Registry.register(Registry.ITEM, new Identifier(MOD_ID, "test_furnace"), new BlockItem(TEST_FURNACE_BLOCK, new Item.Settings().group(ItemGroup.DECORATIONS)));
        //TEST_FURNACE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "test_furnace"), BlockEntityType.Builder.create(TestFurnaceBlockEntity::new, TEST_FURNACE_BLOCK).build(null));

        TEST_RECIPE_TYPE = Registry.register(Registry.RECIPE_TYPE, new Identifier(MOD_ID, "item_sorter"), new RecipeType<SisRecipeType>() {
            @Override
            public String toString() {
                return "test_furnace";
            }
        });

        //TEST_RECIPE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MOD_ID, "test_furnace"), new CookingRecipeSerializer(TestRecipe::new, 200));
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(Inventory inventory) {
        return super.getRemainder(inventory);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return super.isIgnoredInRecipeBook();
    }

    @Override
    public ItemStack createIcon() {
        return super.createIcon();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }


    public static class Serializer extends ShapedRecipe.Serializer
    {
        public SisRecipeType fromJson(ResourceLocation id, JsonObject json) {
            return new SisRecipeType(super.fromJson(id, json));
        }

        public SisRecipeType fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new SisRecipeType(super.fromNetwork(id, buffer));
        }
    }
}