package com.iafenvoy.tameable.mixin;

import com.iafenvoy.tameable.config.TameableConfig;
import com.iafenvoy.tameable.network.NetworkConstants;
import com.iafenvoy.tameable.network.ServerNetworkHelper;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void playerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        ServerNetworkHelper.sendToPlayer(player, NetworkConstants.TAMEABLE_DATA_SYNC, TameableConfig.INSTANCE.toBuffer());
    }
}
