package com.iafenvoy.tameable.forge;

import com.iafenvoy.tameable.Tameable;
import com.iafenvoy.tameable.config.TameableConfig;
import com.iafenvoy.tameable.forge.component.TameableProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Tameable.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TameableForge {
    public TameableForge() {
    }

    @SubscribeEvent
    public static void registerReloadListener(AddReloadListenerEvent event) {
        event.addListener(TameableConfig.INSTANCE);
    }

    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof MobEntity mob && !mob.getCapability(TameableProvider.CAPABILITY).isPresent())
            event.addCapability(new Identifier(Tameable.MOD_ID, "tame_data"), new TameableProvider(mob));
    }
}