package com.github.iunius118.tolaserblade.network;

import com.github.iunius118.tolaserblade.ToLaserBladeConfig;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ServerConfigMessage extends ToLaserBladeConfig.ServerConfig implements IMessage {
    public ServerConfigMessage() {

    }

    public ServerConfigMessage(ToLaserBladeConfig.ServerConfig serverConfig) {
        isEnabledBlockingWithLaserBlade = serverConfig.isEnabledBlockingWithLaserBlade;
        laserBladeEfficiency = serverConfig.laserBladeEfficiency;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        isEnabledBlockingWithLaserBlade = buf.readBoolean();
        laserBladeEfficiency = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isEnabledBlockingWithLaserBlade);
        buf.writeInt(laserBladeEfficiency);
    }
}
