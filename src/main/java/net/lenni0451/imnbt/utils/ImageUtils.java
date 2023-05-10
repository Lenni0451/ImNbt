package net.lenni0451.imnbt.utils;

public class ImageUtils {

    public static float calculateUV(final int textureSize, final int pos) {
        return (float) pos / (float) textureSize;
    }

}
