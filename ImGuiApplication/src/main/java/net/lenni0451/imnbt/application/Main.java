package net.lenni0451.imnbt.application;

import imgui.app.Application;
import org.lwjgl.system.Configuration;

public class Main {

    private static Main instance;

    public static void main(String[] args) {
        instance = new Main();
        instance.init();
    }

    public static Main getInstance() {
        return instance;
    }


    private final Config config = new Config();
    private final FontConfig fontConfig = new FontConfig();
    private final ImGuiImpl imGuiImpl = new ImGuiImpl(this.config, this.fontConfig);

    private void init() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            Configuration.GLFW_CHECK_THREAD0.set(false);
        }
        Application.launch(this.imGuiImpl);
    }

    public ImGuiImpl getImGuiImpl() {
        return this.imGuiImpl;
    }

    public FontConfig getFontConfig() {
        return this.fontConfig;
    }

}
