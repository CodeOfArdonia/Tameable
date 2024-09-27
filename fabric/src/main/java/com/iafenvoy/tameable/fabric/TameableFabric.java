package com.iafenvoy.tameable.fabric;

import com.iafenvoy.tameable.Tameable;
import com.iafenvoy.tameable.config.TameableConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class TameableFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void reload(ResourceManager manager) {
                TameableConfig.INSTANCE.reload(manager);
            }

            @Override
            public Identifier getFabricId() {
                return new Identifier(Tameable.MOD_ID, "tame_config");
            }
        });
    }
}