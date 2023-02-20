package net.lenni0451.imnbt.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12C;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ImageUtils {


    public static int loadTexture(final BufferedImage image) {
        final int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); // 4 for RGBA, 3 for RGB
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        buffer.flip();

        final int textureID = GL11C.glGenTextures();
        GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, textureID);

        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_LINEAR);
        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_LINEAR);
        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_S, GL12C.GL_CLAMP_TO_EDGE);
        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_T, GL12C.GL_CLAMP_TO_EDGE);

        GL11C.glTexImage2D(GL11C.GL_TEXTURE_2D, 0, GL11C.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11C.GL_RGBA, GL11C.GL_UNSIGNED_BYTE, buffer);
        return textureID;
    }

    public static void setIcon(final long window, final InputStream is) throws IOException {
        GLFWImage image = GLFWImage.malloc();
        PNGDecoder decoder = new PNGDecoder(is);
        ByteBuffer imageByteBuf = ByteBuffer.allocateDirect(decoder.getWidth() * decoder.getHeight() * 4);
        decoder.decode(imageByteBuf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        imageByteBuf.flip();
        image.set(decoder.getWidth(), decoder.getHeight(), imageByteBuf);
        GLFWImage.Buffer images = GLFWImage.malloc(1);
        images.put(0, image);
        GLFW.glfwSetWindowIcon(window, images);
        images.free();
        image.free();
    }

    public static float calculateUV(final int textureSize, final int pos) {
        return (float) pos / (float) textureSize;
    }

}
