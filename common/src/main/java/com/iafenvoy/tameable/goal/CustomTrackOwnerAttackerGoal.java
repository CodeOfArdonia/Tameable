package com.iafenvoy.tameable.goal;

import com.iafenvoy.tameable.config.TameableConfig;
import com.iafenvoy.tameable.data.EntityTameData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class CustomTrackOwnerAttackerGoal extends TrackTargetGoal {
    private LivingEntity attacker;
    private int lastAttackedTime;

    public CustomTrackOwnerAttackerGoal(MobEntity mob) {
        super(mob, false);
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (!TameableConfig.INSTANCE.get(this.mob.getType()).getProtect().enable()) return false;
        EntityTameData data = EntityTameData.get(this.mob);
        if (data.getOwner() != null && data.getState() == EntityTameData.State.FOLLOW) {
            PlayerEntity player = data.getOwnerPlayer();
            if (player == null) return false;
            else {
                this.attacker = player.getAttacker();
                int i = player.getLastAttackedTime();
                return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT);
            }
        } else return false;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
        PlayerEntity player = EntityTameData.get(this.mob).getOwnerPlayer();
        if (player != null) this.lastAttackedTime = player.getLastAttackedTime();
        super.start();
    }
}
