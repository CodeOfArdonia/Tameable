package com.iafenvoy.tameable;

import com.iafenvoy.tameable.data.EntityTameData;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.mob.MobEntity;

public class ComponentManager {
    @ExpectPlatform
    public static EntityTameData getEntityData(MobEntity mob) {
        throw new AssertionError("This method should be replaced by Architectury");
    }
}
