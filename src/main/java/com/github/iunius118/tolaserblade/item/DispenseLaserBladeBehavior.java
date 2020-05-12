package com.github.iunius118.tolaserblade.item;

import com.github.iunius118.tolaserblade.util.LaserTrapPlayer;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

public class DispenseLaserBladeBehavior implements IBehaviorDispenseItem {
    @Override
    public ItemStack dispense(IBlockSource source, ItemStack stack) {
        World world = source.getWorld();

        if (world instanceof WorldServer) {
            BlockPos pos = source.getBlockPos();
            EnumFacing dir = source.getBlockState().getValue(BlockDispenser.FACING);

            // Create fake player to attack entities
            LaserTrapPlayer laserTrapPlayer = LaserTrapPlayer.get((WorldServer)world);
            laserTrapPlayer.initInventory(stack.copy());
            BlockPos targetPos = pos.offset(dir);

            attackEntitiesInPos(laserTrapPlayer, targetPos);
        }

        return stack;
    }

    private void attackEntitiesInPos(LaserTrapPlayer laserTrapPlayer, BlockPos pos) {
        AxisAlignedBB boundingBox = new AxisAlignedBB(pos);
        List<Entity> targetEntities = laserTrapPlayer.world.getEntitiesWithinAABBExcludingEntity(null, boundingBox);
        float attackDamage = (float)laserTrapPlayer.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();

        for (Entity targetEntity : targetEntities) {
            targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(laserTrapPlayer), attackDamage);
        }
    }
}
