package com.github.iunius118.tolaserblade.recipe;

import com.github.iunius118.tolaserblade.ToLaserBlade;
import com.github.iunius118.tolaserblade.item.ItemLaserBlade;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

//Recipe Laser Blade - Class 2
public class RecipeLaserBladeClass2 extends ShapedOreRecipe {
    public RecipeLaserBladeClass2(ResourceLocation group, ItemStack result, ShapedPrimer primer) {
        super(group, result, primer);
    }

    // tolaserblade:laser_blade_class_2
    public static class Factory implements IRecipeFactory {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

            ItemStack output = recipe.getRecipeOutput();
            NBTTagCompound nbt = ItemLaserBlade.setPerformanceClass2(output);
            nbt.setBoolean(ItemLaserBlade.KEY_IS_CRAFTING, true);

            ShapedPrimer primer = new ShapedPrimer();
            primer.width = recipe.getWidth();
            primer.height = recipe.getHeight();
            primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
            primer.input = recipe.getIngredients();

            return new RecipeLaserBladeClass2(new ResourceLocation(ToLaserBlade.MOD_ID, "laser_blade_class_2"), output, primer);
        }
    }
}
