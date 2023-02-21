package net.lenni0451.imnbt.ui.types;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiTreeNodeFlags;
import net.lenni0451.imnbt.Main;
import net.lenni0451.imnbt.utils.ImageUtils;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface TagRenderer {

    void render(final Consumer<String> nameEditConsumer, final Runnable deleteListener, final String path, final String name, @Nonnull final INbtTag tag);

    void renderValueEditor(final INbtTag tag);

    default void renderBranch(final String text, final String suffix, final String path, final Runnable renderContextMenu, final Runnable renderChildren) {
        ImGui.pushID(path);
        boolean open = ImGui.treeNodeEx("    " + text + " " + suffix + "###" + text, ImGuiTreeNodeFlags.SpanAvailWidth);
        renderContextMenu.run();
        if (open) {
            renderChildren.run();
            ImGui.treePop();
        }
        ImGui.popID();
    }

    default void renderLeaf(final String text, final String path, final Runnable renderContextMenu) {
        ImGui.pushID(path);
        boolean open = ImGui.treeNodeEx("    " + text + "###" + text, ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.SpanAvailWidth);
        renderContextMenu.run();
        if (open) ImGui.treePop();
        ImGui.popID();
    }

    default void renderIcon(final int index) {
        ImVec2 xy = ImGui.getItemRectMin();
        xy.x += ImGui.getFontSize();

        int nbtTypes = NbtType.values().length - 1;
        int size = ImGui.getFontSize();
        ImGui.getWindowDrawList().addImage(
                Main.getInstance().getIconsTexture(),
                xy.x,
                xy.y,
                xy.x + size,
                xy.y + size,
                ImageUtils.calculateUV(16 * nbtTypes, 16 * index),
                0,
                ImageUtils.calculateUV(16 * nbtTypes, 16 * (index + 1)),
                1
        );
    }

}
