package net.lenni0451.imnbt.utils;

public class ArrayUtils {

    public static byte[] remove(final byte[] array, final int index) {
        final byte[] newArray = new byte[array.length - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, newArray.length - index);
        return newArray;
    }

    public static byte[] insert(final byte[] array, final int index, final byte b) {
        final byte[] newArray = new byte[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, index);
        newArray[index] = b;
        System.arraycopy(array, index, newArray, index + 1, newArray.length - index - 1);
        return newArray;
    }

    public static int[] remove(final int[] array, final int index) {
        final int[] newArray = new int[array.length - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, newArray.length - index);
        return newArray;
    }

    public static int[] insert(final int[] array, final int index, final int i) {
        final int[] newArray = new int[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, index);
        newArray[index] = i;
        System.arraycopy(array, index, newArray, index + 1, newArray.length - index - 1);
        return newArray;
    }

    public static long[] remove(final long[] array, final int index) {
        final long[] newArray = new long[array.length - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, newArray.length - index);
        return newArray;
    }

    public static long[] insert(final long[] array, final int index, final long l) {
        final long[] newArray = new long[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, index);
        newArray[index] = l;
        System.arraycopy(array, index, newArray, index + 1, newArray.length - index - 1);
        return newArray;
    }

}
