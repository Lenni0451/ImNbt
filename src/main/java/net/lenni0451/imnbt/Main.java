package net.lenni0451.imnbt;

import imgui.app.Application;

public class Main {

    public static final String VERSION = "${version}";
    public static final int LINES_PER_PAGE = 500;
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

    private void init() {
        Application.launch(this.imGuiImpl);
    }

    public ImGuiImpl getImGuiImpl() {
        return this.imGuiImpl;
    }

    public Config getConfig() {
        return this.config;
    }

}
