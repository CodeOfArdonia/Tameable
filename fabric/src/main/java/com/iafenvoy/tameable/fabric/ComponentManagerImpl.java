package com.iafenvoy.tameable.fabric;

import com.iafenvoy.tameable.data.EntityTameData;
import com.iafenvoy.tameable.fabric.component.TameComponent;
import net.minecraft.entity.mob.MobEntity;

public class ComponentManagerImpl {
    public static EntityTameData getEntityData(MobEntity mob) {
        return TameComponent.TAME_COMPONENT.get(mob).getData();
    }
}
