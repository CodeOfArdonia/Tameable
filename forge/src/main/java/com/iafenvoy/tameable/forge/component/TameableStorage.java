package com.iafenvoy.tameable.forge.component;

import com.iafenvoy.tameable.data.EntityTameData;
import com.iafenvoy.tameable.network.ServerNetworkHelper;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public class TameableStorage implements INBTSerializable<NbtCompound> {
    private final EntityTameData data;

    public TameableStorage(MobEntity mob) {
        this.data = new EntityTameData(mob);
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound compound = new NbtCompound();
        this.data.writeToNbt(compound);
        return compound;
    }

    @Override
    public void deserializeNBT(NbtCompound compound) {
        this.data.readFromNbt(compound);
    }

    public EntityTameData getData() {
        return this.data;
    }
}
