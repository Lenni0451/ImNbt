package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.mcstructs.snbt.SNbt;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class SNbtUtils {

    public static final Map<String, SNbt<?>> SERIALIZERS = getVersions();
    public static final String[] SERIALIZER_NAMES = getVersionNames();

    /**
     * Get all available {@link SNbt} versions.
     *
     * @return A map with the name to the serializer
     */
    private static Map<String, SNbt<?>> getVersions() {
        Map<String, SNbt<?>> versions = new LinkedHashMap<>();
        try {
            for (Field field : SNbt.class.getDeclaredFields()) {
                if (field.getName().equals("LATEST")) continue;
                if (Modifier.isStatic(field.getModifiers()) && SNbt.class.isAssignableFrom(field.getType())) {
                    versions.put(field.getName(), (SNbt<?>) field.get(null));
                }
            }
        } catch (Throwable ignored) {
        }
        return versions;
    }

    /**
     * Get all version names of the available {@link SNbt} versions.<br>
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
