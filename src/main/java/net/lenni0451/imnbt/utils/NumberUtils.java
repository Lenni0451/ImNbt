package net.lenni0451.imnbt.utils;

import javax.annotation.Nullable;

public class NumberUtils {

    /**
     * Try to parse a string to a byte.
     *
     * @param s The string to parse
     * @return The parsed byte or {@code null} if the string is not a byte
     */
    @Nullable
    public static Byte asByte(final String s) {
        try {
            return Byte.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    /**
     * Try to parse a string to a short.
     *
     * @param s The string to parse
     * @return The parsed short or {@code null} if the string is not a short
     */
    @Nullable
    public static Short asShort(final String s) {
        try {
            return Short.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    /**
     * Try to parse a string to an integer.
     *
     * @param s The string to parse
     * @return The parsed integer or {@code null} if the string is not an integer
     */
    @Nullable
    public static Integer asInt(final String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    /**
     * Try to parse a string to a long.
     *
     * @param s The string to parse
     * @return The parsed long or {@code null} if the string is not a long
     */
    @Nullable
    public static Long asLong(final String s) {
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    /**
     * Try to parse a string to a float.
     *
     * @param s The string to parse
     * @return The parsed float or {@code null} if the string is not a float
     */
    @Nullable
    public static Float asFloat(final String s) {
        try {
            return Float.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    /**
     * Try to parse a string to a double.
     *
     * @param s The string to parse
     * @return The parsed double or {@code null} if the string is not a double
     */
    @Nullable
    public static Double asDouble(final String s) {
        try {
            return Double.valueOf(s);
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    /**
     * Round a float to a given number of decimal places.
     *
     * @param value         The value to round
     * @param decimalPlaces The number of decimal places
     * @return The rounded value
     */
    public static float round(final float value, final int decimalPlaces) {
        return (float) Math.round(value * Math.pow(10, decimalPlaces)) / (float) Math.pow(10, decimalPlaces);
    }

    /**
     * Round a double to a given number of decimal places.
     *
     * @param value         The value to round
     * @param decimalPlaces The number of decimal places
     * @return The rounded value
     */
    public static double round(final double value, final int decimalPlaces) {
        return Math.round(value * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
    }

}
