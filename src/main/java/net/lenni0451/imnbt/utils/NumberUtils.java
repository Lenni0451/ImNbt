package net.lenni0451.imnbt.utils;

import javax.annotation.Nullable;

public class NumberUtils {

    @Nullable
    public static Byte asByte(final String s) {
        try {
            return Byte.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    @Nullable
    public static Short asShort(final String s) {
        try {
            return Short.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    @Nullable
    public static Integer asInt(final String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    @Nullable
    public static Long asLong(final String s) {
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    @Nullable
    public static Float asFloat(final String s) {
        try {
            return Float.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    @Nullable
    public static Double asDouble(final String s) {
        try {
            return Double.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

}
