package com.iafenvoy.tameable.fabric;

import com.iafenvoy.tameable.TameableClient;
import net.fabricmc.api.ClientModInitializer;

public class TameableFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TameableClient.init();
        TameableClient.process();
    }
}
