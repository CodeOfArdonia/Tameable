package com.iafenvoy.tameable.mixin;

import com.iafenvoy.tameable.config.TameableConfig;
import com.iafenvoy.tameable.data.EntityTameData;
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

import java.util.Optional;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    @Shadow
    @Final
    protected GoalSelector goalSelector;

    @Shadow
    @Final
    protected GoalSelector targetSelector;

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("HEAD"))
    private void addTameGoals(CallbackInfo ci) {
        Optional<TameableConfig.TameableData> optional = TameableConfig.INSTANCE.get(this.getType());
        if (optional.isEmpty()) return;
        TameableConfig.TameableData data = optional.get();
        MobEntity t = (MobEntity) (Object) this;
        if (data.attack()) this.targetSelector.add(2, new CustomAttackWithOwnerGoal(t));
        if (data.follow().enable()) {
            TameableConfig.FollowInfo follow = data.follow();
            this.goalSelector.add(1, new CustomFollowOwnerGoal(t, follow.speed(), follow.minDistance(), follow.maxDistance(), follow.leavesAllowed()));
        }
        if (data.protect()) this.targetSelector.add(2, new CustomTrackOwnerAttackerGoal(t));
    }

    @Inject(method = "interactWithItem", at = @At("HEAD"), cancellable = true)
    private void handleFeed(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Optional<TameableConfig.TameableData> optional = TameableConfig.INSTANCE.get(this.getType());
        if (optional.isEmpty()) return;
        TameableConfig.TameableData data = optional.get();
        ItemStack stack = player.getStackInHand(hand);
        if (data.food().stream().anyMatch(x -> x.left().map(stack::isOf).orElse(false) || x.right().map(stack::isIn).orElse(false))) {
            MobEntity t = (MobEntity) (Object) this;
            EntityTameData entityTameData = EntityTameData.get(t);
            if (entityTameData.getOwner() != null) {
                if (this.getHealth() < this.getMaxHealth() && entityTameData.getOwnerPlayer() == player) {
                    if (!player.isCreative()) stack.decrement(1);
                    this.heal(stack.getItem().getFoodComponent().getHunger());
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            } else {
                if (!player.isCreative()) stack.decrement(1);
                if (this.random.nextFloat() < data.chance()) {
                    entityTameData.setOwner(player.getUuid());
                    entityTameData.setSitting(false);
                    this.getWorld().sendEntityStatus(this, (byte) 7);
                    this.tameable$showEmoteParticle(true);
                } else {
                    this.getWorld().sendEntityStatus(this, (byte) 6);
                    this.tameable$showEmoteParticle(false);
                }
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Unique
    private void tameable$showEmoteParticle(boolean positive) {
        ParticleEffect particleEffect = ParticleTypes.HEART;
        if (!positive) particleEffect = ParticleTypes.SMOKE;
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.getWorld().addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }
}
