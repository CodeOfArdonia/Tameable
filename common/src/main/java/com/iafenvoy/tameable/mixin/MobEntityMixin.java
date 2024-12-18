package com.iafenvoy.tameable.mixin;

import com.iafenvoy.tameable.config.TameableConfig;
import com.iafenvoy.tameable.data.EntityTameData;
import com.iafenvoy.tameable.event.TamableInteractionCallback;
import com.iafenvoy.tameable.goal.CustomAttackWithOwnerGoal;
import com.iafenvoy.tameable.goal.CustomFollowOwnerGoal;
import com.iafenvoy.tameable.goal.CustomTrackOwnerAttackerGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    @Shadow
    @Final
    protected GoalSelector goalSelector;

    @Shadow
    @Final
    protected GoalSelector targetSelector;

    @Shadow
    public abstract void setAiDisabled(boolean aiDisabled);

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("HEAD"))
    private void addTameGoals(CallbackInfo ci) {
        if (!this.isAlive()) return;
        MobEntity mob = (MobEntity) (Object) this;
        TameableConfig.TameableData data = TameableConfig.INSTANCE.get(this.getType());
        this.targetSelector.add(data.getAttack().priority(), new CustomAttackWithOwnerGoal(mob));
        this.goalSelector.add(data.getFollow().priority(), new CustomFollowOwnerGoal(mob));
        this.targetSelector.add(data.getProtect().priority(), new CustomTrackOwnerAttackerGoal(mob));
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void handleSit(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player.getStackInHand(hand).isEmpty()) {
            MobEntity mob = (MobEntity) (Object) this;
            if (this.getEntityWorld().isClient) {
                TameableConfig.TameableData data = TameableConfig.INSTANCE.get(this.getType());
                if (TamableInteractionCallback.EVENT.invoker().shouldInteract(mob, player, hand) || data.notEmpty())
                    cir.setReturnValue(ActionResult.SUCCESS);
                return;
            }
            EntityTameData entityTameData = EntityTameData.get(mob);
            if (!Objects.equals(entityTameData.getOwner(), player.getUuid())) return;
            entityTameData.convertSit();
            this.setAiDisabled(entityTameData.getState() == EntityTameData.State.SIT);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(method = "interactWithItem", at = @At("HEAD"), cancellable = true)
    private void handleFeed(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!this.isAlive()) return;
        TameableConfig.TameableData data = TameableConfig.INSTANCE.get(this.getType());
        ItemStack stack = player.getStackInHand(hand);
        MobEntity t = (MobEntity) (Object) this;
        if (this.getEntityWorld().isClient) {
            if (TamableInteractionCallback.EVENT.invoker().shouldInteract(t, player, hand) || data.notEmpty() && this.getHealth() < this.getMaxHealth() && data.canInteract(stack))
                cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }
        EntityTameData entityTameData = EntityTameData.get(t);
        if (entityTameData.getOwner() != null && data.canBreed(stack)) {
            if (this.getHealth() < this.getMaxHealth() && Objects.equals(entityTameData.getOwner(), player.getUuid())) {
                if (!player.isCreative()) {
                    if (stack.isDamageable()) stack.damage(1, this.random, null);
                    else stack.decrement(1);
                }
                this.heal(data.getBreedAmount(stack));
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        } else if (entityTameData.getOwner() == null && data.canTame(stack)) {
            if (!player.isCreative()) {
                if (stack.isDamageable()) stack.damage(1, this.random, null);
                else stack.decrement(1);
            }
            if (this.random.nextFloat() < data.chance()) {
                entityTameData.setOwner(player.getUuid());
                entityTameData.setState(EntityTameData.State.FOLLOW);
                this.getWorld().sendEntityStatus(this, (byte) 7);
            } else this.getWorld().sendEntityStatus(this, (byte) 6);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(method = "handleStatus", at = @At("HEAD"))
    private void handleParticles(byte status, CallbackInfo ci) {
        if (status == 7) this.tameable$showEmoteParticle(true);
        else if (status == 6) this.tameable$showEmoteParticle(false);
    }

    @Unique
    private void tameable$showEmoteParticle(boolean positive) {
        ParticleEffect particleEffect = positive ? ParticleTypes.HEART : ParticleTypes.SMOKE;
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.getWorld().addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }
}
