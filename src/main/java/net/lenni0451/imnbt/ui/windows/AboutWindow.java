package net.lenni0451.imnbt.ui.windows;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.utils.Licenses;
import net.lenni0451.imnbt.utils.NotificationLevel;

import java.awt.*;
import java.net.URI;

/**
 * The window to show information about the program.
 */
public class AboutWindow extends Window {

    public AboutWindow(ImNbtDrawer drawer) {
        super(drawer);
    }

    @Override
    public void render() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.menuItem("Close")) {
                this.hide();
            }
            if (ImGui.menuItem("Github")) {
                this.openURL("https://github.com/Lenni0451/ImNbt");
            }
            if (ImGui.menuItem("Jenkins")) {
                this.openURL("https://build.lenni0451.net/job/ImNbt/");
            }

            ImGui.endMenuBar();
        }

        ImGui.textUnformatted("ImNbt by Lenni0451");
        ImGui.textUnformatted("Version: ${version}");
        ImGui.separator();
        ImGui.textUnformatted("Open Source Licenses");
        if (ImGui.treeNodeEx("Guava", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.textUnformatted(Licenses.GUAVA);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("ImGui", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.textUnformatted(Licenses.IMGUI);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("ImGui-Java", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.textUnformatted(Licenses.IMGUI_JAVA);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("ImNbt", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.textUnformatted(Licenses.IMNBT);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("LWJGL", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.textUnformatted(Licenses.LWJGL);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("MCStructs", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.textUnformatted(Licenses.MCSTRUCTS);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("MCStructs-Bedrock", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.textUnformatted(Licenses.MCSTRUCTS_BEDROCK);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("OpenSans Regular", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.textUnformatted(Licenses.OPENSANS);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("PNGDecoder", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.textUnformatted(Licenses.PNGDECODER);
            ImGui.treePop();
        }
    }

    private void openURL(final String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Throwable t) {
            t.printStackTrace();
            this.drawer.showNotification(NotificationLevel.ERROR, "Error", "Failed to open the URL");
        }
        this.hide();
    }

}
