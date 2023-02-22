package net.lenni0451.imnbt;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.ui.windows.AboutWindow;
import net.lenni0451.imnbt.ui.windows.DiffWindow;
import net.lenni0451.imnbt.ui.windows.MainWindow;
import net.lenni0451.imnbt.utils.imgui.ImageUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;

public class ImGuiImpl extends Application {

    private int iconsTexture;
    private Popup<?> popup;
    private Window window;

    private final MainWindow mainWindow = new MainWindow();
    private final AboutWindow aboutWindow = new AboutWindow();
    private final DiffWindow diffWindow = new DiffWindow();

    public ImGuiImpl() {
        this.window = this.mainWindow;
    }

    public int getIconsTexture() {
        return this.iconsTexture;
    }

    public void openPopup(final Popup<?> popup) {
        this.popup = popup;
    }

    public void closePopup() {
        this.popup = null;
    }

    public MainWindow getMainWindow() {
        return this.mainWindow;
    }

    public AboutWindow getAboutWindow() {
        return this.aboutWindow;
    }

    public DiffWindow getDiffWindow() {
        return this.diffWindow;
    }

    public void showWindow(@Nonnull final Window window) {
        this.window = window;
    }

    @Override
    protected void configure(Configuration config) {
        config.setTitle("ImNbt");
    }

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);
        try {
            ImageUtils.setIcon(handle, Main.class.getClassLoader().getResourceAsStream("assets/logo.png"));
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
        try {
            this.iconsTexture = ImageUtils.loadTexture(ImageIO.read(Main.class.getClassLoader().getResourceAsStream("assets/icons.png")));
            if (this.iconsTexture < 0) throw new IllegalStateException("Failed to load icons texture");
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }

        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setIniFilename(null);
        ImFontAtlas imFontAtlas = imGuiIO.getFonts();
        ImFontConfig imFontConfig = new ImFontConfig();
        imFontConfig.setPixelSnapH(true);
        try {
            ImFont[] fonts = Main.getInstance().getConfig().getFonts();

            byte[] segoeui = ImGuiImpl.class.getClassLoader().getResourceAsStream("assets/segoeui.ttf").readAllBytes();
            imFontAtlas.addFontDefault(imFontConfig);
            for (int i = 0; i < fonts.length; i++) {
                int size = 15 + (5 * i);
                imFontConfig.setName("SegoeUI " + i + "px");
                fonts[i] = imFontAtlas.addFontFromMemoryTTF(segoeui, size, imFontConfig, imFontAtlas.getGlyphRangesDefault());
            }
            imFontAtlas.build();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
        imFontConfig.destroy();

        GLFW.glfwSetDropCallback(this.getHandle(), (window, count, names) -> {
            if (count != 1) return;
            File file = new File(GLFWDropCallback.getName(names, 0));
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] data = fis.readAllBytes();
                this.window.dragAndDrop(file, data);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void dispose() {
        super.dispose();
        System.exit(0);
    }

    @Override
    public void process() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0);
        ImGui.pushFont(Main.getInstance().getConfig().getFonts()[Main.getInstance().getConfig().getUsedFont()]);

        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getIO().getDisplaySize().x, ImGui.getIO().getDisplaySize().y);
        ImGui.begin("MainWindow", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar);
        this.window.render();
        ImGui.end();
        if (this.popup != null) {
            this.popup.open();
            this.popup.render();
        }

        ImGui.popFont();
        ImGui.popStyleVar();
    }

}
