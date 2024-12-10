package com.iafenvoy.tameable.data;

import com.iafenvoy.tameable.ComponentManager;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EntityTameData {
    private final MobEntity mob;
    @Nullable
    private UUID owner = null;
    private State state = State.FOLLOW;
    private boolean isDirty;

    public EntityTameData(MobEntity mob) {
        this.mob = mob;
    }

    public void markDirty() {
        this.isDirty = true;
    }

    public boolean isDirty() {
        boolean b = this.isDirty;
        this.isDirty = false;
        return b;
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
        this.state = this.state.next();
        this.markDirty();
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
        this.markDirty();
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        this.markDirty();
    }

    public void writeToNbt(NbtCompound nbt) {
        if (this.owner != null) nbt.putUuid("owner", this.owner);
        nbt.putString("sitting", this.state.name());
    }

    public void readFromNbt(NbtCompound nbt) {
        if (nbt.contains("owner")) this.owner = nbt.getUuid("owner");
        if (nbt.contains("sitting", NbtElement.STRING_TYPE))
            this.state = State.valueOf(nbt.getString("sitting"));
    }

    public static EntityTameData get(MobEntity mob) {
        return ComponentManager.getEntityData(mob);
    }

    public enum State {
        SIT("↓"),
        FOLLOW("→"),
        WANDER("×");

        private final String symbol;

        State(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return this.symbol;
        }

        public State next() {
            return switch (this) {
                case SIT -> FOLLOW;
                case FOLLOW -> WANDER;
                case WANDER -> SIT;
            };
        }
    }
}
