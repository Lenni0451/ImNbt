package net.lenni0451.imnbt.ui;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import net.lenni0451.mcstructs.nbt.INbtTag;

public abstract class TagRenderer {

    public abstract void render(final String name, final INbtTag tag);

    protected void renderBranch(final String text, final int hashCode, final Runnable renderChildren) {
        if (ImGui.treeNodeEx(text + "##" + hashCode, ImGuiTreeNodeFlags.SpanAvailWidth)) {
            renderChildren.run();
            ImGui.treePop();
        }
    }

    protected void renderLeaf(final String text, final int hashCode) {
        if (ImGui.treeNodeEx(text + "##" + hashCode, ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.treePop();
        }
    }

}
