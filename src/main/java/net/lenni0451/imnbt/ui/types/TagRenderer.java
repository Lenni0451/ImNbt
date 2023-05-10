package net.lenni0451.imnbt.ui.types;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.imnbt.utils.ImageUtils;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TagRenderer {

    void render(final ImNbtDrawer drawer, final Consumer<String> nameEditConsumer, final Runnable deleteListener, final Function<String, Color> colorProvider, final SearchProvider searchProvider, final boolean openContextMenu, final String path, final String name, @Nonnull final INbtTag tag);

    void renderValueEditor(final INbtTag tag);

    default void renderBranch(final String text, final String suffix, final String path, final Runnable renderContextMenu, final Runnable renderChildren, final Function<String, Color> colorProvider, final SearchProvider searchProvider) {
        ImGui.pushID(path);
        Color color = colorProvider.apply(path);
        if (color != null) ImGui.pushStyleColor(ImGuiCol.Text, color.getABGR());
        if (searchProvider.isExpanded(path)) ImGui.setNextItemOpen(true);
        boolean open = ImGui.treeNodeEx("    " + text + " " + suffix + "###    " + text, ImGuiTreeNodeFlags.SpanAvailWidth);
        if (color != null) ImGui.popStyleColor();
        renderContextMenu.run();
        if (open) {
            renderChildren.run();
            ImGui.treePop();
        }
        ImGui.popID();
    }

    default void renderLeaf(final String text, final String suffix, final String path, final Runnable renderContextMenu, final Function<String, Color> colorProvider, final SearchProvider searchProvider) {
        ImGui.pushID(path);
        Color color = colorProvider.apply(path);
        if (color != null) ImGui.pushStyleColor(ImGuiCol.Text, color.getABGR());
        if (searchProvider.isExpanded(path)) ImGui.setNextItemOpen(true);
        boolean open = ImGui.treeNodeEx("    " + text + suffix, ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.SpanAvailWidth);
        if (color != null) ImGui.popStyleColor();
        renderContextMenu.run();
        if (open) ImGui.treePop();
        ImGui.popID();
    }

    default void handleSearch(final SearchProvider searchProvider, final String path) {
        if (searchProvider.isSearched(path)) {
            ImVec2 start = ImGui.getItemRectMin();
            ImVec2 end = ImGui.getItemRectMax();
            Color color = new Color(255, 255, 255, 64);
            if (searchProvider.isTargeted(path)) color = new Color(255, 255, 0, 64);

            if (start.y >= 0 && start.y <= ImGui.getIO().getDisplaySizeY()) ImGui.getWindowDrawList().addRectFilled(start.x, start.y, end.x, end.y, color.getABGR());

            if (searchProvider.shouldDoScroll(path)) ImGui.setScrollHereY();
        }
    }

    default void renderIcon(final ImNbtDrawer drawer, final int index) {
        ImVec2 xy = ImGui.getItemRectMin();
        xy.x += ImGui.getFontSize();
        if (xy.y < 0 || xy.y > ImGui.getIO().getDisplaySizeY()) return;

        int nbtTypes = NbtType.values().length - 1;
        int size = ImGui.getFontSize();
        ImGui.getWindowDrawList().addImage(
                drawer.getIconsTexture(),
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
