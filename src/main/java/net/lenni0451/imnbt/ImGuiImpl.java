package net.lenni0451.imnbt;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import net.lenni0451.imnbt.ui.MainWindow;
import net.lenni0451.imnbt.utils.PNGDecoder;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12C;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ImGuiImpl extends Application {

    private static ImGuiImpl instance;

    public static ImGuiImpl getInstance() {
        return instance;
    }

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


    private final ImFont[] fonts = new ImFont[5];
    private int usedFont = 3;
    private int iconsTexture;

    private final MainWindow mainWindow = new MainWindow();

    public ImGuiImpl() {
        instance = this;
    }

    public ImFont[] getFonts() {
        return this.fonts;
    }

    public int getUsedFont() {
        return this.usedFont;
    }

    public void setUsedFont(final int usedFont) {
        this.usedFont = usedFont;
    }

    public int getIconsTexture() {
        return this.iconsTexture;
    }

    public MainWindow getMainWindow() {
        return this.mainWindow;
    }

    @Override
    protected void configure(Configuration config) {
        config.setTitle("ImNbt");
    }

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);

        ImGuiIO imGuiIO = ImGui.getIO();
        ImFontAtlas imFontAtlas = imGuiIO.getFonts();
        ImFontConfig imFontConfig = new ImFontConfig();
        imFontConfig.setPixelSnapH(true);
        try {
            byte[] segoeui = ImGuiImpl.class.getClassLoader().getResourceAsStream("assets/segoeui.ttf").readAllBytes();
            imFontAtlas.addFontDefault(imFontConfig);
            for (int i = 0; i < this.fonts.length; i++) {
                int size = 15 + (5 * i);
                imFontConfig.setName("SegoeUI " + i + "px");
                this.fonts[i] = imFontAtlas.addFontFromMemoryTTF(segoeui, size, imFontConfig, imFontAtlas.getGlyphRangesDefault());
            }
            imFontAtlas.build();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
        imFontConfig.destroy();

        try {
            this.iconsTexture = loadTexture(ImageIO.read(ImGuiImpl.class.getClassLoader().getResourceAsStream("assets/icons.png")));
            if (this.iconsTexture < 0) throw new IllegalStateException("Failed to load icons texture");
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }

        try {
            this.setIcon(ImGuiImpl.class.getClassLoader().getResourceAsStream("assets/logo.png"));
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    protected void dispose() {
        super.dispose();
        System.exit(0);
    }

    @Override
    public void process() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0);
        ImGui.pushFont(this.fonts[usedFont]);

        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getIO().getDisplaySize().x, ImGui.getIO().getDisplaySize().y);
        ImGui.begin("MainWindow", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar);
        this.mainWindow.render();
        ImGui.end();

        ImGui.popFont();
        ImGui.popStyleVar();
    }

    private void setIcon(final InputStream is) throws IOException {
        GLFWImage image = GLFWImage.malloc();
        PNGDecoder decoder = new PNGDecoder(is);
        ByteBuffer imageByteBuf = ByteBuffer.allocateDirect(decoder.getWidth() * decoder.getHeight() * 4);
        decoder.decode(imageByteBuf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        imageByteBuf.flip();
        image.set(decoder.getWidth(), decoder.getHeight(), imageByteBuf);
        GLFWImage.Buffer images = GLFWImage.malloc(1);
        images.put(0, image);
        GLFW.glfwSetWindowIcon(this.getHandle(), images);
        images.free();
        image.free();
    }

}
