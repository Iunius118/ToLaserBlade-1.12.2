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

//Recipe Laser Blade - Class 3
public class RecipeLaserBladeClass3 extends ShapedOreRecipe {
    public RecipeLaserBladeClass3(ResourceLocation group, ItemStack result, ShapedPrimer primer) {
        super(group, result, primer);
    }

    // tolaserblade:laser_blade_class_3
    public static class Factory implements IRecipeFactory {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

            ItemStack output = recipe.getRecipeOutput();
            NBTTagCompound nbt = ItemLaserBlade.setPerformanceClass3(output, ItemLaserBlade.colors[0]);
            nbt.setBoolean(ItemLaserBlade.KEY_IS_CRAFTING, true);

            ShapedPrimer primer = new ShapedPrimer();
            primer.width = recipe.getWidth();
            primer.height = recipe.getHeight();
            primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
            primer.input = recipe.getIngredients();

            return new RecipeLaserBladeClass3(new ResourceLocation(ToLaserBlade.MOD_ID, "laser_blade_class_3"), output, primer);
        }
    }
}
