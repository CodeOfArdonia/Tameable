package com.iafenvoy.tameable.data;

import com.iafenvoy.tameable.ComponentManager;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class EntityTameData {
    private final MobEntity mob;
    @Nullable
    private UUID owner = null;
    private boolean sitting = false;
    private final Consumer<MobEntity> syncFunc;

    public EntityTameData(MobEntity mob, Consumer<MobEntity> syncFunc) {
        this.mob = mob;
        this.syncFunc = syncFunc;
    }

    public MobEntity getMob() {
        return this.mob;
    }

    @Nullable
    public UUID getOwner() {
        return this.owner;
    }

    @Nullable
    public PlayerEntity getOwnerPlayer() {
        if (this.owner == null) return null;
        return this.mob.getEntityWorld().getPlayerByUuid(this.owner);
    }

    public void convertSit() {
        this.sitting = !this.sitting;
        this.syncFunc.accept(this.mob);
    }

    public boolean isSitting() {
        return this.sitting;
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
        this.syncFunc.accept(this.mob);
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        this.syncFunc.accept(this.mob);
    }

    public void writeToNbt(NbtCompound nbt) {
        if (this.owner != null) nbt.putUuid("owner", this.owner);
        nbt.putBoolean("sitting", this.sitting);
    }

    public void readFromNbt(NbtCompound nbt) {
        if (nbt.contains("owner")) this.owner = nbt.getUuid("owner");
        this.sitting = nbt.getBoolean("sitting");
    }

    public static EntityTameData get(MobEntity mob) {
        return ComponentManager.getEntityData(mob);
    }
}
