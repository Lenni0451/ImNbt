package net.lenni0451.imnbt;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import net.lenni0451.imnbt.ui.MainWindow;

public class ImGuiImpl extends Application {

    private static ImGuiImpl instance;

    public static ImGuiImpl getInstance() {
        return instance;
    }


    private final ImFont[] fonts = new ImFont[5];
    private int usedFont = 3;

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

}
