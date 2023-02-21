package net.lenni0451.imnbt;

import imgui.app.Application;
import net.lenni0451.imnbt.ui.windows.MainWindow;

public class Main {

    public static final String VERSION = "${version}";
    private static Main instance;

    public static void main(String[] args) {
        instance = new Main();
        instance.init();
    }

    public static Main getInstance() {
        return instance;
    }


    private final ImGuiImpl imGuiImpl = new ImGuiImpl();
    private final Config config = new Config();
    private int iconsTexture;

    private final MainWindow mainWindow = new MainWindow();

    private void init() {
        Application.launch(this.imGuiImpl);
    }

    public ImGuiImpl getImGuiImpl() {
        return this.imGuiImpl;
    }

    public Config getConfig() {
        return this.config;
    }

    public int getIconsTexture() {
        return this.iconsTexture;
    }

    void setIconsTexture(final int textureId) {
        this.iconsTexture = textureId;
    }

    public MainWindow getMainWindow() {
        return this.mainWindow;
    }

}
