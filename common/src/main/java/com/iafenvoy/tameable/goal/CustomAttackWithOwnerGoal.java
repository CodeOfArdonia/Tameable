package com.iafenvoy.tameable.goal;

import com.iafenvoy.tameable.config.TameableConfig;
import com.iafenvoy.tameable.data.EntityTameData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class CustomAttackWithOwnerGoal extends TrackTargetGoal {
    private LivingEntity attacking;
    private int lastAttackTime;

    public CustomAttackWithOwnerGoal(MobEntity mob) {
        super(mob, false);
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (!TameableConfig.INSTANCE.get(this.mob.getType()).attack()) return false;
        EntityTameData data = EntityTameData.get(this.mob);
        if (data.getOwner() != null && !data.isSitting()) {
            PlayerEntity player = data.getOwnerPlayer();
            if (player == null) return false;
            else {
                this.attacking = player.getAttacking();
                int i = player.getLastAttackTime();
                return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT);
            }
        } else return false;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacking);
        PlayerEntity player = EntityTameData.get(this.mob).getOwnerPlayer();
        if (player != null) this.lastAttackTime = player.getLastAttackTime();
        super.start();
    }
}
