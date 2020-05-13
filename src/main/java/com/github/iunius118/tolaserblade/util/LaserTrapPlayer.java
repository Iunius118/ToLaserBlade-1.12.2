package com.github.iunius118.tolaserblade.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

public class LaserTrapPlayer extends FakePlayer {
    private static final GameProfile PROFILE = new GameProfile(UUID.fromString("2BDD19A3-9616-417A-8797-EE805F5FF9E3"), "[LaserBlade]");

    private LaserTrapPlayer(WorldServer world) {
        super(world, PROFILE);
    }

    public static LaserTrapPlayer get(WorldServer world) {
        return new LaserTrapPlayer(world);
    }

    public void initInventory(ItemStack currentStack) {
        inventory.clear();

        // Set given item stack on main hand
        inventory.currentItem = 0;
        inventory.setInventorySlotContents(0, currentStack);

        // Apply attack damage from main hand item
        this.getAttributeMap().applyAttributeModifiers(currentStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
    }

    @Override
    public void updateHeldItem() {
    }

    @Override
    public void sendEnterCombat() {
    }

    @Override
    public void sendEndCombat() {
    }
}
