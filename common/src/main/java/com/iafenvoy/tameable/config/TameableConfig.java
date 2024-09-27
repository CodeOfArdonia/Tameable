package com.iafenvoy.tameable.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iafenvoy.tameable.Tameable;
import com.iafenvoy.tameable.util.JsonUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public enum TameableConfig implements SynchronousResourceReloader {
    INSTANCE;
    private static final String PATH = "./config/tameable.json";
    private final Map<EntityType<?>, TameableData> data = new HashMap<>();

    @Override
    public void reload(ResourceManager manager) {
        this.data.clear();
        try {
            if (!Files.exists(Path.of(PATH))) FileUtils.write(new File(PATH), "[]", StandardCharsets.UTF_8);
            JsonArray root = JsonParser.parseReader(new FileReader(PATH)).getAsJsonArray();
            for (JsonElement element : root) {
                JsonObject obj = element.getAsJsonObject();
                EntityType<?> type = Registries.ENTITY_TYPE.get(Identifier.tryParse(obj.get("type").getAsString()));
                this.data.put(type, new TameableData(obj));
            }
        } catch (Exception e) {
            Tameable.LOGGER.error("Failed to load tameable config:", e);
        }
    }

    public Optional<TameableData> get(EntityType<?> type) {
        return Optional.ofNullable(this.data.get(type));
    }

    public record TameableData(List<Ingredient> food, double chance, boolean attack, FollowInfo follow,
                               boolean protect) {
        public TameableData(JsonObject obj) {
            this(obj.get("food").getAsJsonArray().asList().stream().map(Ingredient::fromJson).toList(),
                    JsonUtils.getDoubleOrDefault(obj, "chance", 1),
                    JsonUtils.getBooleanOrDefault(obj, "attack", false),
                    new FollowInfo(obj.get("follow").getAsJsonObject()),
                    JsonUtils.getBooleanOrDefault(obj, "protect", false));
        }
    }

    public record FollowInfo(boolean enable, double speed, float minDistance, float maxDistance,
                             boolean leavesAllowed) {
        public FollowInfo(JsonObject obj) {
            this(JsonUtils.getBooleanOrDefault(obj, "enable", false),
                    JsonUtils.getDoubleOrDefault(obj, "speed", 1),
                    JsonUtils.getFloatOrDefault(obj, "minDistance", 10),
                    JsonUtils.getFloatOrDefault(obj, "maxDistance", 2),
                    JsonUtils.getBooleanOrDefault(obj, "leavesAllowed", false));
        }
    }
}
