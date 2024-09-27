package com.iafenvoy.tameable.data;

import com.iafenvoy.tameable.ComponentManager;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EntityTameData {
    private final MobEntity mob;
    @Nullable
    private UUID owner = null;
    private boolean sitting = false;

    public EntityTameData(MobEntity mob) {
        this.mob = mob;
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
    }

    public boolean isSitting() {
        return this.sitting;
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
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
