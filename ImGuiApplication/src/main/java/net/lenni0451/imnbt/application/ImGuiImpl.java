package net.lenni0451.imnbt.application;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.application.clipboard.NbtClipboardContent;
import net.lenni0451.imnbt.application.clipboard.NbtDataFlavor;
import net.lenni0451.imnbt.application.utils.FileDialogs;
import net.lenni0451.imnbt.application.utils.ImageUtils;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.ui.windows.AboutWindow;
import net.lenni0451.imnbt.ui.windows.DiffWindow;
import net.lenni0451.imnbt.ui.windows.MainWindow;
import net.lenni0451.imnbt.utils.NotificationLevel;
import net.lenni0451.mcstructs.nbt.io.NamedTag;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;

public class ImGuiImpl extends Application implements ImNbtDrawer {

    private final Config config;
    private final MainWindow mainWindow;
    private final AboutWindow aboutWindow;
    private final DiffWindow diffWindow;

    private int iconsTexture;
    private Popup<?> popup;
    private Window window;

    public ImGuiImpl(final Config config, final FontConfig fontConfig) {
        this.config = config;
        this.mainWindow = new MainWindow(this, fontConfig);
        this.aboutWindow = new AboutWindow(this);
        this.diffWindow = new DiffWindow(this);

        this.window = this.mainWindow;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    @Override
    public int getLinesPerPage() {
        return 500;
    }

    @Override
    public int getIconsTexture() {
        return this.iconsTexture;
    }

    @Override
    public void openPopup(final @Nonnull Popup<?> popup) {
        this.popup = popup;
    }

    @Override
    public void showNotification(NotificationLevel level, String title, String message, Runnable callback) {
        Notifications.add(level, title, message);
        callback.run();
    }

    @Override
    public void closePopup() {
        this.popup = null;
    }

    @Override
    public MainWindow getMainWindow() {
        return this.mainWindow;
    }

    @Override
    public AboutWindow getAboutWindow() {
        return this.aboutWindow;
    }

    @Override
    public DiffWindow getDiffWindow() {
        return this.diffWindow;
    }

    @Override
    public void showWindow(@Nonnull final Window window) {
        this.window = window;
    }

    @Nullable
    @Override
    public String showOpenFileDialog(String title) {
        return FileDialogs.open(title);
    }

    @Nullable
    @Override
    public String showSaveFileDialog(String title) {
        return FileDialogs.save(title);
    }

    @Override
    public boolean hasClipboard() {
        return NbtDataFlavor.isInSystemClipboard();
    }

    @Override
    public void setClipboard(@Nonnull NamedTag tag) {
        new NbtClipboardContent(tag).setSystemClipboard();
    }

    @Override
    public NamedTag getClipboard() {
        try {
            return NbtClipboardContent.getFromSystemClipboard();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    @Override
    protected void configure(Configuration config) {
        config.setTitle("ImNbt");
    }

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);
        try {
            ImageUtils.setIcon(handle, Main.class.getClassLoader().getResourceAsStream("imnbt/logo.png"));
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
        try {
            this.iconsTexture = ImageUtils.loadTexture(ImageIO.read(Main.class.getClassLoader().getResourceAsStream("imnbt/icons.png")));
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
            ImFont[] fonts = Main.getInstance().getFontConfig().getFonts();

            byte[] opensans_regular = ImGuiImpl.class.getClassLoader().getResourceAsStream("imnbt/OpenSans-Regular.ttf").readAllBytes();
            imFontAtlas.addFontDefault(imFontConfig);
            for (int i = 0; i < fonts.length; i++) {
                int size = 15 + (5 * i);
                imFontConfig.setName("OpenSans Regular " + i + "px");
                fonts[i] = imFontAtlas.addFontFromMemoryTTF(opensans_regular, size, imFontConfig, imFontAtlas.getGlyphRangesDefault());
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
        ImGui.pushFont(Main.getInstance().getFontConfig().getFont());

        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getIO().getDisplaySize().x, ImGui.getIO().getDisplaySize().y);
        ImGui.begin("MainWindow", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar);
        this.window.render();
        if (this.window instanceof MainWindow) {
            if (KeyboardHelper.isCtrlPressed()) {
                if (ImGui.isKeyPressed(KeyboardHelper.KEY_F)) {
                    this.mainWindow.highlightSearch();
                } else if (ImGui.isKeyPressed(KeyboardHelper.KEY_Z)) {
                    this.mainWindow.undo();
                } else if (ImGui.isKeyPressed(KeyboardHelper.KEY_Y)) {
                    this.mainWindow.redo();
                } else if (ImGui.isKeyPressed(KeyboardHelper.KEY_S)) {
                    this.mainWindow.saveCurrent();
                }
            }
        }
        ImGui.end();
        if (this.popup != null) {
            this.popup.open();
            this.popup.render(this);
        }

        Notifications.draw();

        ImGui.popFont();
        ImGui.popStyleVar();
    }

    @Override
    public void exit() {
        System.exit(0);
    }

}
