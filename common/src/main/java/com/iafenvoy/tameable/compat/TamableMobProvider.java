package com.iafenvoy.tameable.compat;

import com.iafenvoy.tameable.Tameable;
import com.iafenvoy.tameable.data.EntityTameData;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.util.CommonProxy;

import java.util.UUID;

public enum TamableMobProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;
    private static final String OWNER_KEY = "OwnerUuid";
    private static final String SIT_KEY = "Sitting";

    @Override
    public Identifier getUid() {
        return new Identifier(Tameable.MOD_ID, "tameable");
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        UUID uuid;
        EntityTameData.State sitting;
        if (entityAccessor.getServerData().contains(OWNER_KEY)) {
            uuid = entityAccessor.getServerData().getUuid(OWNER_KEY);
            sitting = EntityTameData.State.valueOf(entityAccessor.getServerData().getString(SIT_KEY));
        } else {
            if (!(entityAccessor.getEntity() instanceof MobEntity mob)) return;
            EntityTameData data = EntityTameData.get(mob);
            if (data.getOwner() != null) {
                uuid = data.getOwner();
                sitting = data.getState();
            } else return;
        }
        String name = CommonProxy.getLastKnownUsername(uuid);
        if (name == null) name = "???";
        iTooltip.add(Text.translatable("jade.owner", name).append(Text.literal(sitting.getSymbol())));
    }

    @Override
    public void appendServerData(NbtCompound nbt, EntityAccessor entityAccessor) {
        if (!(entityAccessor.getEntity() instanceof MobEntity mob)) return;
        EntityTameData data = EntityTameData.get(mob);
        if (data.getOwner() != null) {
            nbt.putUuid(OWNER_KEY, data.getOwner());
            nbt.putString(SIT_KEY, data.getState().name());
        }
    }
}
