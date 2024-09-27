package com.iafenvoy.tameable;

import com.iafenvoy.tameable.config.TameableConfig;
import com.mojang.logging.LogUtils;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;

public class Tameable {
    public static final String MOD_ID = "tameable";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ReloadListenerRegistry.register(ResourceType.SERVER_DATA, TameableConfig.INSTANCE);
    }
}
