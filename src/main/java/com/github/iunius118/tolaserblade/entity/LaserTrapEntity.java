package com.github.iunius118.tolaserblade.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LaserTrapEntity extends Entity {
    private int life = 3;

    public LaserTrapEntity(World worldIn) {
        super(worldIn);
        isImmuneToFire = true;
        setSize(1.0F, 1.0F);
    }

    public LaserTrapEntity(World worldIn, BlockPos pos, EnumFacing direction) {
        this(worldIn);
        setPosition((double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D);
        setRotation(direction.getHorizontalAngle(), (float)(direction.getDirectionVec().getY() * -90));
    }

    @Override
    protected void entityInit() {

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
        life = compound.getInteger("life");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("life", life);
    }
}
