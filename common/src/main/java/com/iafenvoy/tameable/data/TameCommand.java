package com.iafenvoy.tameable.data;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class TameCommand {
    public static final LiteralArgumentBuilder<ServerCommandSource> TAME = CommandManager.literal("tame")
            .requires(x -> !x.getServer().isDedicated() || x.hasPermissionLevel(2))
            .then(CommandManager.argument("entityUuid", UuidArgumentType.uuid())
                    .executes(context -> {
                        ServerWorld world = context.getSource().getWorld();
                        Entity entity = world.getEntity(UuidArgumentType.getUuid(context, "entityUuid"));
                        if (entity instanceof MobEntity mob)
                            EntityTameData.get(mob).setOwner(null);
                        return 1;
                    }).then(CommandManager.argument("ownerUuid", UuidArgumentType.uuid())
                            .executes(context -> {
                                ServerWorld world = context.getSource().getWorld();
                                Entity entity = world.getEntity(UuidArgumentType.getUuid(context, "entityUuid"));
                                if (entity instanceof MobEntity mob)
                                    EntityTameData.get(mob).setOwner(UuidArgumentType.getUuid(context, "ownerUuid"));
                                return 1;
                            })));
}
