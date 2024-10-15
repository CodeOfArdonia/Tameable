package com.iafenvoy.tameable.network.forge;

import com.iafenvoy.tameable.forge.network.ClientNetworkContainer;
import com.iafenvoy.tameable.network.ClientNetworkHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientNetworkHelperImpl {
    public static void sendToServer(Identifier id, PacketByteBuf buf) {
        ClientNetworkContainer.sendToServer(id, buf);
    }

    public static void registerReceiver(Identifier id, ClientNetworkHelper.Handler handler) {
        ClientNetworkContainer.registerReceiver(id, handler);
    }
}
