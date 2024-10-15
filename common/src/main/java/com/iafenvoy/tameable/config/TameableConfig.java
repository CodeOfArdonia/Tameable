package com.iafenvoy.tameable.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iafenvoy.tameable.Static;
import com.iafenvoy.tameable.Tameable;
import com.iafenvoy.tameable.network.ByteBufUtil;
import com.iafenvoy.tameable.network.NetworkConstants;
import com.iafenvoy.tameable.network.ServerNetworkHelper;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.entity.EntityType;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TameableConfig implements SynchronousResourceReloader {
    INSTANCE;
    private static final Codec<Either<Item, TagKey<Item>>> ITEM_OR_TAG = Codec.either(Registries.ITEM.getCodec(), TagKey.codec(RegistryKeys.ITEM));
    private static final Codec<TameableData> DATAPACK_CODEC = RecordCodecBuilder.create(i1 -> i1.group(
            ITEM_OR_TAG.listOf().fieldOf("tame").forGetter(TameableData::tame),
            Codec.either(ITEM_OR_TAG, RecordCodecBuilder.<BreedFoodData>create(i3 -> i3.group(
                    ITEM_OR_TAG.fieldOf("item").forGetter(BreedFoodData::item),
                    Codec.INT.optionalFieldOf("heal", 1).forGetter(BreedFoodData::heal)
            ).apply(i3, BreedFoodData::new))).listOf().optionalFieldOf("breed", new ArrayList<>()).forGetter(TameableData::breed),
            Codec.DOUBLE.optionalFieldOf("chance", 1D).forGetter(TameableData::chance),
            Codec.BOOL.optionalFieldOf("attack", false).forGetter(TameableData::attack),
            RecordCodecBuilder.<FollowInfo>create(i2 -> i2.group(
                    Codec.BOOL.optionalFieldOf("enable", false).forGetter(FollowInfo::enable),
                    Codec.DOUBLE.optionalFieldOf("speed", 1D).forGetter(FollowInfo::speed),
                    Codec.FLOAT.optionalFieldOf("minDistance", 10F).forGetter(FollowInfo::minDistance),
                    Codec.FLOAT.optionalFieldOf("maxDistance", 2F).forGetter(FollowInfo::maxDistance),
                    Codec.BOOL.optionalFieldOf("leavesAllowed", false).forGetter(FollowInfo::leavesAllowed)
            ).apply(i2, FollowInfo::new)).optionalFieldOf("follow", FollowInfo.EMPTY).forGetter(TameableData::follow),
            Codec.BOOL.optionalFieldOf("protect", false).forGetter(TameableData::protect)
    ).apply(i1, TameableData::new));
    private static final UnboundedMapCodec<EntityType<?>, TameableData> CONFIG_CODEC = Codec.unboundedMap(Registries.ENTITY_TYPE.getCodec(), DATAPACK_CODEC);
    private static final String PATH = "./config/tameable.json";
    private final Map<EntityType<?>, TameableData> data = new HashMap<>();

    @Override
    public void reload(ResourceManager manager) {
        this.data.clear();
        //Firstly, read data from datapack(s)
        for (Map.Entry<Identifier, Resource> entry : manager.findResources("tameable", p -> p.getPath().endsWith(".json")).entrySet()) {
            Identifier id = entry.getKey();
            Resource resource = entry.getValue();
            Identifier entity = new Identifier(id.getNamespace(), id.getPath().replace("tameable/", "").replace(".json", ""));
            if (!Registries.ENTITY_TYPE.containsId(entity)) {
                Tameable.LOGGER.error("Cannot find entity {} in tameable data of {}", entity, resource.getResourcePackName());
                continue;
            }
            try {
                JsonElement element = JsonParser.parseReader(new InputStreamReader(resource.getInputStream()));
                DataResult<TameableData> d = DATAPACK_CODEC.parse(JsonOps.INSTANCE, element);
                this.data.put(Registries.ENTITY_TYPE.get(entity), d.resultOrPartial(Tameable.LOGGER::error).orElseThrow());
            } catch (Exception e) {
                Tameable.LOGGER.error("Failed to load {} from datapack {}.", entity, resource.getResourcePackName(), e);
            }

        }
        //Then, read from config.
        try {
            if (!Files.exists(Path.of(PATH))) FileUtils.write(new File(PATH), "{}", StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseReader(new FileReader(PATH));
            DataResult<Map<EntityType<?>, TameableData>> result = CONFIG_CODEC.parse(JsonOps.INSTANCE, element);
            this.data.putAll(result.resultOrPartial(Tameable.LOGGER::error).orElseThrow());
        } catch (Exception e) {
            Tameable.LOGGER.error("Failed to load tameable config.", e);
        }
        Tameable.LOGGER.info("Successfully loaded {} entity tame config.", this.data.keySet().size());
        //Finally, send to all players
        if (Static.server != null && Static.server.isRunning() && Static.server.isDedicated()) {
            PacketByteBuf buf = this.toBuffer();
            for (ServerPlayerEntity serverPlayer : Static.server.getPlayerManager().getPlayerList())
                ServerNetworkHelper.sendToPlayer(serverPlayer, NetworkConstants.TAMEABLE_DATA_SYNC, buf);
            Tameable.LOGGER.info("Successfully send tame config to {} players.", Static.server.getPlayerManager().getPlayerList().size());
        }
    }

    public PacketByteBuf toBuffer() {
        return ByteBufUtil.create().writeString(CONFIG_CODEC.encodeStart(JsonOps.INSTANCE, this.data).resultOrPartial(Tameable.LOGGER::error).orElseThrow().toString());
    }

    public void loadFromServer(String data) {
        Tameable.LOGGER.info("Receive data from server, loading...");
        this.data.clear();
        try {
            JsonElement element = JsonParser.parseString(data);
            DataResult<Map<EntityType<?>, TameableData>> result = CONFIG_CODEC.parse(JsonOps.INSTANCE, element);
            this.data.putAll(result.resultOrPartial(Tameable.LOGGER::error).orElseThrow());
        } catch (Exception e) {
            Tameable.LOGGER.error("Failed to load from server tameable config.", e);
        }
        Tameable.LOGGER.info("Successfully loaded {} entity tame config from server.", this.data.keySet().size());
    }

    public TameableData get(EntityType<?> type) {
        return this.data.getOrDefault(type, TameableData.DEFAULT);
    }

    public static boolean match(Either<Item, TagKey<Item>> either, ItemStack stack) {
        return either.left().map(stack::isOf).orElse(false) || either.right().map(stack::isIn).orElse(false);
    }

    public record TameableData(List<Either<Item, TagKey<Item>>> tame,
                               List<Either<Either<Item, TagKey<Item>>, BreedFoodData>> breed,
                               double chance, boolean attack, FollowInfo follow, boolean protect) {
        public static TameableData DEFAULT = new TameableData(new ArrayList<>(), new ArrayList<>(), 0, false, FollowInfo.EMPTY, false);

        public boolean canTame(ItemStack stack) {
            return this.tame.stream().anyMatch(x -> match(x, stack));
        }

        public boolean canBreed(ItemStack stack) {
            return this.breed.isEmpty() ? this.canTame(stack) : this.breed.stream().anyMatch(x -> x.left().map(y -> match(y, stack)).orElse(false) || x.right().map(y -> match(y.item, stack)).orElse(false));
        }

        public int getBreedAmount(ItemStack stack) {
            if (this.breed.isEmpty() || this.breed.stream().anyMatch(x -> x.left().map(y -> match(y, stack)).orElse(false))) {
                FoodComponent component = stack.getItem().getFoodComponent();
                return component == null ? 1 : component.getHunger();
            }
            return this.breed.stream().filter(x -> x.right().map(y -> match(y.item, stack)).orElse(false)).findFirst().map(x -> x.right().map(y -> y.heal).orElse(1)).orElse(1);
        }

        public boolean isEmpty() {
            return this == DEFAULT;
        }
    }

    public record BreedFoodData(Either<Item, TagKey<Item>> item, int heal) {
    }

    public record FollowInfo(boolean enable, double speed, float minDistance, float maxDistance,
                             boolean leavesAllowed) {
        public static final FollowInfo EMPTY = new FollowInfo(false, 1, 10, 2, false);
    }
}
