package com.iafenvoy.tameable.mixin;

import com.iafenvoy.tameable.data.EntityTameData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onDeath", at = @At("RETURN"))
    private void showDeathMessage(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity l = (LivingEntity) (Object) this;
        if (!(l instanceof MobEntity mob)) return;
        EntityTameData data = EntityTameData.get(mob);
        if (!this.getWorld().isClient && this.getWorld().getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && data.getOwnerPlayer() instanceof ServerPlayerEntity player) {
            player.sendMessage(mob.getDamageTracker().getDeathMessage());
        }
    }
}
