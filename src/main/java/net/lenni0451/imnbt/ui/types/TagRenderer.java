package net.lenni0451.imnbt.ui.types;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import net.lenni0451.mcstructs.nbt.INbtTag;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface TagRenderer {

    void render(final Consumer<String> nameEditConsumer, final String name, @Nonnull final INbtTag tag);

    void renderValueEditor(final INbtTag tag);

    default void renderBranch(final String text, final int hashCode, final Runnable renderContextMenu, final Runnable renderChildren) {
        ImGui.pushID(hashCode);
        boolean open = ImGui.treeNodeEx(text, ImGuiTreeNodeFlags.SpanAvailWidth);
        renderContextMenu.run();
        if (open) {
            renderChildren.run();
            ImGui.treePop();
        }
        ImGui.popID();
    }

    default void renderLeaf(final String text, final int hashCode, final Runnable renderContextMenu) {
        ImGui.pushID(hashCode);
        boolean open = ImGui.treeNodeEx(text, ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.SpanAvailWidth);
        renderContextMenu.run();
        if (open) ImGui.treePop();
        ImGui.popID();
    }

}
