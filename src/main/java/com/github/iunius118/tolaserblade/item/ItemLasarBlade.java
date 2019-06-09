package com.github.iunius118.tolaserblade.item;

import com.github.iunius118.tolaserblade.ToLaserBlade;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLasarBlade extends ItemSword {
    private final Item.ToolMaterial material;
    private final float attackDamage;
    private final float attackSpeed;

    public ItemLasarBlade() {
        super(ToLaserBlade.MATERIAL_LASAR);

        setCreativeTab(CreativeTabs.TOOLS);
        material = ToLaserBlade.MATERIAL_LASAR;
        attackDamage = 3.0F + material.getAttackDamage();
        attackSpeed = -1.2F;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (itemstack.isEmpty() && !(itemstack.getItem() instanceof ItemLasarBlade)) {
            return EnumActionResult.PASS;
        }

        IBlockState blockstate = worldIn.getBlockState(pos);
        Block block = blockstate.getBlock();
        int costDamage = material.getMaxUses() / 2 + 1;

        // Redstone Torch -> Repairing/Collecting

        if (block == Blocks.REDSTONE_TORCH && player.canPlayerEdit(pos, facing, itemstack)) {
            int itemDamage = itemstack.getItemDamage();
            if (itemDamage >= costDamage || player.capabilities.isCreativeMode) {
                // Repair this
                itemstack.setItemDamage(itemDamage - costDamage);
            } else {
                // Collect a Redstone Torch
                if (!player.inventory.addItemStackToInventory(new ItemStack(Blocks.REDSTONE_TORCH))) {
                    // Cannot collect because player's inventory is full
                    return EnumActionResult.FAIL;
                }
            }

            // Destroy the Redstone Torch block
            if (worldIn.isRemote) {
                Minecraft.getMinecraft().renderGlobal.playEvent(player, 2001, pos, Block.getStateId(blockstate));
            }
            worldIn.setBlockToAir(pos);

            return EnumActionResult.SUCCESS;
        }

        // Damage -> Redstone Torch

        if (!player.capabilities.isCreativeMode && itemstack.getItemDamage() >= costDamage) {
            // This is too damaged to place Redstone Torch
            return EnumActionResult.FAIL;
        }

        if (!block.isReplaceable(worldIn, pos)) {
            pos = pos.offset(facing);
        }

        if (player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(Blocks.REDSTONE_TORCH, pos, false, facing, null)) {
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState iblockstate1 = Blocks.REDSTONE_TORCH.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

            if (new ItemBlock(Blocks.REDSTONE_TORCH).placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1)) {
                // Place a Redstone Torch and Damage this
                iblockstate1 = worldIn.getBlockState(pos);
                SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.damageItem(costDamage, player);
            }

            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

        if (slot == EntityEquipmentSlot.MAINHAND) {
            multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage, 0));
            multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, 0));
        }

        return multimap;
    }
}
