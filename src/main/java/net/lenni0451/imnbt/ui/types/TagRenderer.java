package net.lenni0451.imnbt.ui.types;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import net.lenni0451.mcstructs.nbt.INbtTag;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface TagRenderer {

    void render(final Consumer<String> nameEditConsumer, final String name, @Nonnull final INbtTag tag);

    void renderValueEditor(final INbtTag tag);

    default void renderBranch(final String text, final int hashCode, final Runnable renderChildren) {
        if (ImGui.treeNodeEx(text + "##" + hashCode, ImGuiTreeNodeFlags.SpanAvailWidth)) {
            renderChildren.run();
            ImGui.treePop();
        }
    }

    default void renderLeaf(final String text, final int hashCode, final Runnable renderContextMenu) {
        if (ImGui.treeNodeEx(text + "##" + hashCode, ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.SpanAvailWidth)) {
            renderContextMenu.run();
            ImGui.treePop();
        }
    }

}
