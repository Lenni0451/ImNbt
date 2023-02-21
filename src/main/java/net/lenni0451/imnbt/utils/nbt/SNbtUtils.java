package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.mcstructs.snbt.SNbtSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class SNbtUtils {

    public static final Map<String, SNbtSerializer<?>> SERIALIZERS = getVersions();
    public static final String[] SERIALIZER_NAMES = getVersionNames();

    private static Map<String, SNbtSerializer<?>> getVersions() {
        Map<String, SNbtSerializer<?>> versions = new LinkedHashMap<>();
        try {
            for (Field field : SNbtSerializer.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && SNbtSerializer.class.isAssignableFrom(field.getType())) {
                    versions.put(field.getName(), (SNbtSerializer<?>) field.get(null));
                }
            }
        } catch (Throwable ignored) {
        }
        return versions;
    }

    private static String[] getVersionNames() {
        String[] names = new String[SERIALIZERS.size()];
        int i = 0;
        for (String name : SERIALIZERS.keySet()) names[i++] = name.substring(1).replace('_', '.');
        return names;
    }

}
