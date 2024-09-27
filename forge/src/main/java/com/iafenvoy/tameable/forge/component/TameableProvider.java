package com.iafenvoy.tameable.forge.component;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TameableProvider implements ICapabilitySerializable<NbtCompound> {
    public static final Capability<TameableStorage> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    private final TameableStorage storage;
    private final LazyOptional<TameableStorage> storageLazyOptional = LazyOptional.of(this::getOrCreateStorage);

    public TameableProvider(MobEntity mob) {
        this.storage = new TameableStorage(mob);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        return CAPABILITY.orEmpty(capability, this.storageLazyOptional);
    }

    @Override
    public NbtCompound serializeNBT() {
        return this.storage.serializeNBT();
    }

    @Override
    public void deserializeNBT(NbtCompound arg) {
        this.storage.deserializeNBT(arg);
    }

    private TameableStorage getOrCreateStorage() {
        return this.storage;
    }
}
