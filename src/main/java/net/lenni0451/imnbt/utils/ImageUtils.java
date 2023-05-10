package net.lenni0451.imnbt.utils;

public class ImageUtils {

    /**
     * Calculate the UV for a texture.
     *
     * @param textureSize The size of the texture
     * @param pos         The pixel position
     * @return The UV
     */
    public static float calculateUV(final int textureSize, final int pos) {
        return (float) pos / (float) textureSize;
    }

}
