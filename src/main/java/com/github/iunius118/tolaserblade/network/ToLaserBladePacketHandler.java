package com.github.iunius118.tolaserblade.network;

import com.github.iunius118.tolaserblade.ToLaserBlade;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ToLaserBladePacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ToLaserBlade.MOD_ID);

    public static void init() {
        INSTANCE.registerMessage(ServerConfigMessageHandler.class, ServerConfigMessage.class, 0, Side.CLIENT);
    }
}
