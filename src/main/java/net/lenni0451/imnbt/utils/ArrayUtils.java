package net.lenni0451.imnbt.utils;

public class ArrayUtils {

    /**
     * Remove a byte from an array at the given index.
     *
     * @param array The array to remove the byte from
     * @param index The index of the byte to remove
     * @return The new array without the byte
     */
    public static byte[] remove(final byte[] array, final int index) {
        final byte[] newArray = new byte[array.length - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, newArray.length - index);
        return newArray;
    }

    /**
     * Insert a byte into an array at the given index.
     *
     * @param array The array to insert the byte into
     * @param index The index to insert the byte at
     * @param b     The byte to insert
     * @return The new array with the byte inserted
     */
    public static byte[] insert(final byte[] array, final int index, final byte b) {
        final byte[] newArray = new byte[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, index);
        newArray[index] = b;
        System.arraycopy(array, index, newArray, index + 1, newArray.length - index - 1);
        return newArray;
    }

    /**
     * Remove an int from an array at the given index.
     *
     * @param array The array to remove the int from
     * @param index The index of the int to remove
     * @return The new array without the int
     */
    public static int[] remove(final int[] array, final int index) {
        final int[] newArray = new int[array.length - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, newArray.length - index);
        return newArray;
    }

    /**
     * Insert an int into an array at the given index.
     *
     * @param array The array to insert the int into
     * @param index The index to insert the int at
     * @param i     The int to insert
     * @return The new array with the int inserted
     */
    public static int[] insert(final int[] array, final int index, final int i) {
        final int[] newArray = new int[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, index);
        newArray[index] = i;
        System.arraycopy(array, index, newArray, index + 1, newArray.length - index - 1);
        return newArray;
    }

    /**
     * Remove a long from an array at the given index.
     *
     * @param array The array to remove the long from
     * @param index The index of the long to remove
     * @return The new array without the long
     */
    public static long[] remove(final long[] array, final int index) {
        final long[] newArray = new long[array.length - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, newArray.length - index);
        return newArray;
    }

    /**
     * Insert a long into an array at the given index.
     *
     * @param array The array to insert the long into
     * @param index The index to insert the long at
     * @param l     The long to insert
     * @return The new array with the long inserted
     */
    public static long[] insert(final long[] array, final int index, final long l) {
        final long[] newArray = new long[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, index);
        newArray[index] = l;
        System.arraycopy(array, index, newArray, index + 1, newArray.length - index - 1);
        return newArray;
    }

}
