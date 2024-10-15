package com.iafenvoy.tameable;

import com.iafenvoy.tameable.config.TameableConfig;
import com.iafenvoy.tameable.network.ClientNetworkHelper;
import com.iafenvoy.tameable.network.NetworkConstants;

public class TameableClient {
    public static void init() {
    }

    public static void process() {
        ClientNetworkHelper.registerReceiver(NetworkConstants.TAMEABLE_DATA_SYNC, (client, buf) -> {
            String data = buf.readString();
            return () -> TameableConfig.INSTANCE.loadFromServer(data);
        });
    }
}
