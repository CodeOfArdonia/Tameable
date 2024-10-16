package com.iafenvoy.tameable.fabric;

import com.iafenvoy.tameable.Tameable;
import com.iafenvoy.tameable.data.TameCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class TameableFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Tameable.init();
        Tameable.process();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new TameableConfigReloader());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(TameCommand.TAME));
    }
}