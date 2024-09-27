package com.iafenvoy.tameable.forge;

import com.iafenvoy.tameable.Tameable;
import com.iafenvoy.tameable.forge.component.TameableProvider;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Tameable.MOD_ID)
public class TameableForge {
    public TameableForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Tameable.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Tameable.init();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof MobEntity mob && !mob.getCapability(TameableProvider.CAPABILITY).isPresent())
                event.addCapability(new Identifier(Tameable.MOD_ID, "tame_data"), new TameableProvider(mob));
        }
    }
}