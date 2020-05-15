package com.github.iunius118.tolaserblade.item;

import com.github.iunius118.tolaserblade.ToLaserBladeConfig;
import com.github.iunius118.tolaserblade.entity.LaserTrapEntity;
import com.github.iunius118.tolaserblade.util.LaserTrapPlayer;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;


public class DispenseLaserBladeBehavior implements IBehaviorDispenseItem {
    public static final Predicate<Entity> LASER_TRAP_TARGETS = Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, Entity::canBeCollidedWith);

    @Override
    public ItemStack dispense(IBlockSource source, ItemStack stack) {
        if (!ToLaserBladeConfig.common.isEnabledLaserTrap) {
            return BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(null).dispense(source, stack);
        }

        World world = source.getWorld();

        if (world instanceof WorldServer) {
            BlockPos pos = source.getBlockPos();
            EnumFacing dir = source.getBlockState().getValue(BlockDispenser.FACING);

            // Create fake player to attack entities
            LaserTrapPlayer laserTrapPlayer = LaserTrapPlayer.get((WorldServer)world);
            laserTrapPlayer.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            laserTrapPlayer.initInventory(stack.copy());
            BlockPos targetPos = pos.offset(dir);

            // Attack entities
            attackEntitiesInPos(laserTrapPlayer, targetPos);

            // Kill fake player
            laserTrapPlayer.setDead();

            // Spawn laser entity for laser effect
            int color = ItemLaserBlade.getColorFromNBT(stack, ItemLaserBlade.KEY_COLOR_HALO, ItemLaserBlade.KEY_IS_SUB_COLOR_HALO, ItemLaserBlade.colors[0]);
            LaserTrapEntity laserTrapEntity = new LaserTrapEntity(world, targetPos, dir, color);
            world.spawnEntity(laserTrapEntity);
        }

        return stack;
    }

    private void attackEntitiesInPos(LaserTrapPlayer laserTrapPlayer, BlockPos pos) {
        AxisAlignedBB boundingBox = new AxisAlignedBB(pos).grow(0.5D);
        // Get target entities
        List<Entity> targetEntities = laserTrapPlayer.world.getEntitiesInAABBexcluding(null, boundingBox, LASER_TRAP_TARGETS);
        // Get attack damage
        float attackDamage = (float)laserTrapPlayer.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();

        for (Entity targetEntity : targetEntities) {
            // Attack entities
            targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(laserTrapPlayer), attackDamage);
        }
    }
}
