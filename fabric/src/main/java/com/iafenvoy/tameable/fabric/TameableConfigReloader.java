package com.iafenvoy.tameable.fabric;

import com.iafenvoy.tameable.Tameable;
import com.iafenvoy.tameable.config.TameableConfig;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class TameableConfigReloader implements SimpleSynchronousResourceReloadListener {
    @Override
    public void reload(ResourceManager manager) {
        TameableConfig.INSTANCE.reload(manager);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Tameable.MOD_ID, "tame_config");
    }
}
