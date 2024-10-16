package com.iafenvoy.tameable.forge;

import com.iafenvoy.tameable.data.EntityTameData;
import com.iafenvoy.tameable.forge.component.TameableProvider;
import com.iafenvoy.tameable.forge.component.TameableStorage;
import net.minecraft.entity.mob.MobEntity;

public class ComponentManagerImpl {
    public static EntityTameData getEntityData(MobEntity mob) {
        return mob.getCapability(TameableProvider.CAPABILITY).map(TameableStorage::getData).orElse(new EntityTameData(mob));
    }
}
