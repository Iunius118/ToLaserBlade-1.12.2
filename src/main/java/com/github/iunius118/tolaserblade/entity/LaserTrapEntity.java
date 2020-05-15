package com.github.iunius118.tolaserblade.entity;

import com.github.iunius118.tolaserblade.item.ItemLaserBlade;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LaserTrapEntity extends Entity {
    private int life = 3;
    private int color = ItemLaserBlade.colors[0];

    public static final String KEY_LIFE = "life";
    public static final String KEY_COLOR = "color";
    public static final DataParameter<Integer> PARAM_COLOR = EntityDataManager.createKey(Entity.class, DataSerializers.VARINT);

    public LaserTrapEntity(World worldIn) {
        super(worldIn);
        isImmuneToFire = true;
        setSize(1.0F, 1.0F);
    }

    public LaserTrapEntity(World worldIn, BlockPos pos, EnumFacing direction, int bladeColor) {
        this(worldIn);
        setPosition((double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D);
        setRotation(direction.getHorizontalAngle(), (float)(direction.getDirectionVec().getY() * -90));
        color = bladeColor;
        dataManager.set(PARAM_COLOR, color);
    }

    @Override
    protected void entityInit() {
        dataManager.register(PARAM_COLOR, ItemLaserBlade.colors[0]);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (--life < 0) {
            setDead();
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        life = compound.getInteger(KEY_LIFE);
        color = compound.getInteger(KEY_COLOR);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger(KEY_LIFE, life);
        compound.setInteger(KEY_COLOR, color);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);

        if (PARAM_COLOR.equals(key)) {
            color = getColorDataManager();
        }
    }

    public int getColor() {
        return color;
    }

    public int getColorDataManager() {
        return dataManager.get(PARAM_COLOR);
    }
}
