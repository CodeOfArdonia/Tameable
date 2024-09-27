package com.iafenvoy.tameable.fabric.component;

import com.iafenvoy.tameable.Tameable;
import com.iafenvoy.tameable.data.EntityTameData;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class TameComponent implements ComponentV3, AutoSyncedComponent {
    public static final ComponentKey<TameComponent> TAME_COMPONENT = ComponentRegistryV3.INSTANCE.getOrCreate(Identifier.of(Tameable.MOD_ID, "tame_data"), TameComponent.class);

    private final EntityTameData data;

    public TameComponent(MobEntity mob) {
        this.data = new EntityTameData(mob);
    }

    public EntityTameData getData() {
        return this.data;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.data.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        this.data.writeToNbt(tag);
    }
}
