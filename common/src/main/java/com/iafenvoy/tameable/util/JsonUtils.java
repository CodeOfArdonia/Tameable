package com.iafenvoy.tameable.util;

import com.google.gson.JsonObject;

public class JsonUtils {
    public static boolean getBooleanOrDefault(JsonObject obj, String name, boolean defaultValue) {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
            try {
                return obj.get(name).getAsBoolean();
            } catch (Exception ignored) {
            }
        return defaultValue;
    }

    public static float getFloatOrDefault(JsonObject obj, String name, float defaultValue) {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
            try {
                return obj.get(name).getAsFloat();
            } catch (Exception ignored) {
            }
        return defaultValue;
    }

    public static double getDoubleOrDefault(JsonObject obj, String name, double defaultValue) {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
            try {
                return obj.get(name).getAsDouble();
            } catch (Exception ignored) {
            }
        return defaultValue;
    }
}
