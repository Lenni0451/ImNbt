package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.mcstructs.snbt.SNbtSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class SNbtUtils {

    public static final Map<String, SNbtSerializer<?>> SERIALIZERS = getVersions();
    public static final String[] SERIALIZER_NAMES = getVersionNames();

    /**
     * Get all available {@link SNbtSerializer} versions.
     *
     * @return A map with the name to the serializer
     */
    private static Map<String, SNbtSerializer<?>> getVersions() {
        Map<String, SNbtSerializer<?>> versions = new LinkedHashMap<>();
        try {
            for (Field field : SNbtSerializer.class.getDeclaredFields()) {
                if (field.getName().equals("LATEST")) continue;
                if (Modifier.isStatic(field.getModifiers()) && SNbtSerializer.class.isAssignableFrom(field.getType())) {
                    versions.put(field.getName(), (SNbtSerializer<?>) field.get(null));
                }
            }
        } catch (Throwable ignored) {
        }
        return versions;
    }

    /**
     * Get all version names of the available {@link SNbtSerializer} versions.<br>
     * The names are formatted like this:<br>
     * "V1_14" -> "1.14"
     *
     * @return An array with all version names
     */
    private static String[] getVersionNames() {
        String[] names = new String[SERIALIZERS.size()];
        int i = 0;
        for (String name : SERIALIZERS.keySet()) names[i++] = name.substring(1).replace('_', '.');
        return names;
    }

}
