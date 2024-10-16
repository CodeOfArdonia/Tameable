package com.iafenvoy.tameable.event;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

//This will only be invoked on client
@FunctionalInterface
public interface TamableInteractionCallback {
    Event<TamableInteractionCallback> EVENT = new Event<>(callbacks -> (mob, player, hand) -> {
        for (TamableInteractionCallback callback : callbacks)
            if (callback.shouldInteract(mob, player, hand))
                return true;
        return false;
    });

    boolean shouldInteract(MobEntity mob, PlayerEntity player, Hand hand);
}
