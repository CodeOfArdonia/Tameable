package com.iafenvoy.tameable.mixin;

import com.iafenvoy.tameable.data.EntityTameData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(TargetPredicate.class)
public class TargetPredicateMixin {
    @Shadow
    @Nullable
    private Predicate<LivingEntity> predicate;

    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    private void handleOwner(LivingEntity baseEntity, LivingEntity targetEntity, CallbackInfoReturnable<Boolean> cir) {
        if (!targetEntity.isPartOfGame()) return;
        if (this.predicate != null && !this.predicate.test(targetEntity)) return;
        if (baseEntity instanceof MobEntity mob) {
            EntityTameData data = EntityTameData.get(mob);
            if (data.getOwner() != null && data.getOwnerPlayer() == targetEntity)
                cir.setReturnValue(false);
        }
    }
}
