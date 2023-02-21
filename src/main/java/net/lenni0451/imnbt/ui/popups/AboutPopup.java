package net.lenni0451.imnbt.ui.popups;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import net.lenni0451.imnbt.Main;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.utils.Licenses;

import java.awt.*;
import java.net.URI;

public class AboutPopup extends Popup<AboutPopup> {

    public AboutPopup(final PopupCallback<AboutPopup> callback) {
        super("About", callback);
    }

    @Override
    public void render() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getIO().getDisplaySize().x, ImGui.getIO().getDisplaySize().y);
        ImGui.begin("About", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar);
        this.renderContent();
        ImGui.end();
    }

    @Override
    protected void renderContent() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.menuItem("Close")) {
                this.getCallback().onClose(this, true);
                this.close();
            }
            if (ImGui.menuItem("Github")) {
                this.openURL("https://github.com/Lenni0451/ImNbt");
            }
            if (ImGui.menuItem("Jenkins")) {
                this.openURL("https://build.lenni0451.net/job/ImNbt/");
            }

            ImGui.endMenuBar();
        }

        ImGui.text("ImNbt by Lenni0451");
        ImGui.text("Version: " + Main.VERSION);
        ImGui.separator();
        ImGui.text("Open Source Licenses");
        if (ImGui.treeNodeEx("Guava", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.text(Licenses.GUAVA);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("ImGui", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.text(Licenses.IMGUI);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("ImGui-Java", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.text(Licenses.IMGUI_JAVA);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("ImNbt", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.text(Licenses.IMNBT);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("LWJGL", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.text(Licenses.LWJGL);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("MCStructs", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.text(Licenses.MCSTRUCTS);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("MCStructs-Bedrock", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.text(Licenses.MCSTRUCTS_BEDROCK);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("PNGDecoder", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.text(Licenses.PNGDECODER);
            ImGui.treePop();
        }
    }

    private void openURL(final String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
            this.getCallback().onClose(this, true);
        } catch (Throwable t) {
            t.printStackTrace();
            this.getCallback().onClose(this, false);
        }
        this.close();
    }

}
