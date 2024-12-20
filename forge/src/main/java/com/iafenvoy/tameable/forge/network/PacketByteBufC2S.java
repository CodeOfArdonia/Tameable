package com.iafenvoy.tameable.forge.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketByteBufC2S {
    private final Identifier id;
    private final PacketByteBuf buf;

    public PacketByteBufC2S(Identifier id, PacketByteBuf buf) {
        this.id = id;
        this.buf = buf;
    }

    public static void encode(PacketByteBufC2S message, PacketByteBuf buf) {
        buf.writeIdentifier(message.id).writeBytes(message.buf);
    }

    public static PacketByteBufC2S decode(PacketByteBuf buf) {
        return new PacketByteBufC2S(buf.readIdentifier(), buf);
    }

    public static void handle(PacketByteBufC2S message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (ServerNetworkContainer.onReceive(message.id, message.buf, context))
            context.setPacketHandled(true);
    }
}
