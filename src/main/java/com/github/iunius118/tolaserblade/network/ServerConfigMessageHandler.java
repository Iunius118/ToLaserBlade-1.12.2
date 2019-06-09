package com.github.iunius118.tolaserblade.network;

import com.github.iunius118.tolaserblade.ToLaserBlade;
import com.github.iunius118.tolaserblade.ToLaserBladeConfig;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ServerConfigMessageHandler implements IMessageHandler<ServerConfigMessage, IMessage> {
    private static final Marker SVRCFGMSG_MARKER = MarkerManager.getMarker("ServerConfigMessage");

    @Override
    public IMessage onMessage(ServerConfigMessage message, MessageContext ctx) {
        ToLaserBladeConfig.server.isEnabledBlockingWithLaserBlade = message.isEnabledBlockingWithLaserBlade;
        ToLaserBladeConfig.server.laserBladeEfficiency = message.laserBladeEfficiency;

        ToLaserBlade.logger.info(SVRCFGMSG_MARKER, "config.common.enabledBlockingWithLaserBladeInServer: {}", ToLaserBladeConfig.server.isEnabledBlockingWithLaserBlade);
        ToLaserBlade.logger.info(SVRCFGMSG_MARKER, "config.common.laserBladeEfficiency: {}", ToLaserBladeConfig.server.laserBladeEfficiency);

        return null;
    }
}
