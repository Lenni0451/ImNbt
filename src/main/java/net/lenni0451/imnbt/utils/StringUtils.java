package net.lenni0451.imnbt.utils;

public class StringUtils {

    public static String format(final Enum<?> enumValue) {
        String[] parts = enumValue.name().split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            builder.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase()).append(" ");
        }
        return builder.toString().trim();
    }

}
