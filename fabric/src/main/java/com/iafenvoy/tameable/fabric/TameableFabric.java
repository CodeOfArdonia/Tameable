package com.iafenvoy.tameable.fabric;

import com.iafenvoy.tameable.Tameable;
import net.fabricmc.api.ModInitializer;

public class TameableFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Tameable.init();
    }
}