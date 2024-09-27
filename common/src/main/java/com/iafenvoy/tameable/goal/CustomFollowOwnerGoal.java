package com.iafenvoy.tameable.goal;

import com.iafenvoy.tameable.data.EntityTameData;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.EnumSet;

public class CustomFollowOwnerGoal extends Goal {
    private final MobEntity mob;
    private LivingEntity owner;
    private final WorldView world;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final boolean leavesAllowed;

    public CustomFollowOwnerGoal(MobEntity mob, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
        this.mob = mob;
        this.world = mob.getWorld();
        this.speed = speed;
        this.navigation = mob.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.leavesAllowed = leavesAllowed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        PlayerEntity player = EntityTameData.get(this.mob).getOwnerPlayer();
        if (player == null) return false;
        else if (player.isSpectator()) return false;
        else if (this.cannotFollow()) return false;
        else if (this.mob.squaredDistanceTo(player) < (double) (this.minDistance * this.minDistance)) return false;
        else {
            this.owner = player;
            return true;
        }
    }

    public boolean shouldContinue() {
        if (this.navigation.isIdle()) return false;
        else if (this.cannotFollow()) return false;
        else return !(this.mob.squaredDistanceTo(this.owner) <= (double) (this.maxDistance * this.maxDistance));
    }

    private boolean cannotFollow() {
        return EntityTameData.get(this.mob).getOwner() == null || EntityTameData.get(this.mob).isSitting() || this.mob.hasVehicle() || this.mob.isLeashed();
    }

    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.mob.getPathfindingPenalty(PathNodeType.WATER);
        this.mob.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.mob.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    public void tick() {
        this.mob.getLookControl().lookAt(this.owner, 10.0F, (float) this.mob.getMaxLookPitchChange());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.getTickCount(10);
            if (this.mob.squaredDistanceTo(this.owner) >= 144.0) this.tryTeleport();
            else this.navigation.startMovingTo(this.owner, this.speed);
        }
    }

    private void tryTeleport() {
        BlockPos blockPos = this.owner.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
            if (bl) return;
        }
    }

    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double) x - this.owner.getX()) < 2.0 && Math.abs((double) z - this.owner.getZ()) < 2.0)
            return false;
        else if (!this.canTeleportTo(new BlockPos(x, y, z))) return false;
        else {
            this.mob.refreshPositionAndAngles((double) x + 0.5, y, (double) z + 0.5, this.mob.getYaw(), this.mob.getPitch());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this.world, pos.mutableCopy());
        if (pathNodeType != PathNodeType.WALKABLE)
            return false;
        else {
            BlockState blockState = this.world.getBlockState(pos.down());
            if (!this.leavesAllowed && blockState.getBlock() instanceof LeavesBlock) return false;
            else
                return this.world.isSpaceEmpty(this.mob, this.mob.getBoundingBox().offset(pos.subtract(this.mob.getBlockPos())));
        }
    }

    private int getRandomInt(int min, int max) {
        return this.mob.getRandom().nextInt(max - min + 1) + min;
    }
}
