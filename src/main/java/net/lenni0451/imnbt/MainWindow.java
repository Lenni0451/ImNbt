package net.lenni0451.imnbt;

import imgui.ImFont;
import imgui.ImGui;

public class MainWindow {

    public static void render() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Open")) {

                }
                if (ImGui.menuItem("Save")) {

                }
                ImGui.separator();
                if (ImGui.menuItem("Exit")) {
                    System.exit(0);
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Font Size")) {
                ImFont[] fonts = ImGuiImpl.getInstance().getFonts();
                int usedFont = ImGuiImpl.getInstance().getUsedFont();
                for (int i = 0; i < fonts.length; i++) {
                    ImFont font = fonts[i];
                    if (ImGui.menuItem("Size " + String.format("%.0f", font.getFontSize()), "", i == usedFont)) {
                        ImGuiImpl.getInstance().setUsedFont(i);
                    }
                }

                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }
    }

}
