package com.iafenvoy.tameable.forge;

import com.iafenvoy.tameable.Tameable;
import com.iafenvoy.tameable.TameableClient;
import com.iafenvoy.tameable.config.TameableConfig;
import com.iafenvoy.tameable.data.TameCommand;
import com.iafenvoy.tameable.forge.component.TameableProvider;
import com.iafenvoy.tameable.forge.network.PacketByteBufC2S;
import com.iafenvoy.tameable.forge.network.PacketByteBufS2C;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(Tameable.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TameableForge {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new Identifier(Tameable.MOD_ID, "buf"), () -> "1", s -> true, s -> true);

    public TameableForge() {
        Tameable.init();
        if (EffectiveSide.get() == LogicalSide.CLIENT)
            TameableClient.init();
    }

    @SubscribeEvent
    public static void process(FMLCommonSetupEvent event) {
        event.enqueueWork(Tameable::process);
        CHANNEL.registerMessage(0, PacketByteBufC2S.class, PacketByteBufC2S::encode, PacketByteBufC2S::decode, PacketByteBufC2S::handle);
        CHANNEL.registerMessage(1, PacketByteBufS2C.class, PacketByteBufS2C::encode, PacketByteBufS2C::decode, PacketByteBufS2C::handle);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void registerReloadListener(AddReloadListenerEvent event) {
            event.addListener(TameableConfig.INSTANCE);
        }

        @SubscribeEvent
        public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof MobEntity mob && !mob.getCapability(TameableProvider.CAPABILITY).isPresent())
                event.addCapability(new Identifier(Tameable.MOD_ID, "tame_data"), new TameableProvider(mob));
        }

        @SubscribeEvent
        public static void registerCommand(RegisterCommandsEvent event) {
            event.getDispatcher().register(TameCommand.TAME);
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void processClient(FMLClientSetupEvent event) {
            event.enqueueWork(TameableClient::process);
        }
    }
}