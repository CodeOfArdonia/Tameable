package com.iafenvoy.tameable.mixin;

import com.iafenvoy.tameable.data.EntityTameData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrackTargetGoal.class)
public class TrackTargetGoalMixin {
    @Shadow
    @Final
    protected MobEntity mob;

    @Inject(method = "shouldContinue", at = @At("HEAD"), cancellable = true)
    private void ignoreOwner(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = this.mob.getTarget();
        EntityTameData data = EntityTameData.get(this.mob);
        if (data.getOwner() != null && data.getOwnerPlayer() == livingEntity) cir.setReturnValue(false);
    }
}
