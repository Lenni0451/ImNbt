package net.lenni0451.imnbt.ui.types;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.NbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The renderer for a tag type.
 */
public interface TagRenderer {

    /**
     * Render the tree node for the tag.
     *
     * @param drawer               The drawer instance
     * @param nameEditConsumer     The listener for when the name of a tag is edited
     * @param transformListener    The listener for when the tag is transformed
     * @param deleteListener       The listener for when the tag is deleted
     * @param modificationListener The listener for when the tag is modified
     * @param colorProvider        The provider for the color of the tag
     * @param searchProvider       The provider for the search
     * @param openContextMenu      Whether the context menu should be opened
     * @param path                 The path of the tag
     * @param name                 The name of the tag
     * @param tag                  The tag to render
     */
    void render(final ImNbtDrawer drawer, final Consumer<String> nameEditConsumer, final BiConsumer<String, NbtTag> transformListener, final Runnable deleteListener, final Runnable modificationListener, final Function<String, Color> colorProvider, final SearchProvider searchProvider, final boolean openContextMenu, final String path, final String name, @Nonnull final NbtTag tag);

    /**
     * Render the value editor for the tag.
     *
     * @param tag The tag to render
     */
    void renderValueEditor(final NbtTag tag);

    /**
     * Render a branch in the tree.<br>
     * Used for array/list/compound tags.
     *
     * @param text              The text to display
     * @param suffix            The suffix to display
     * @param path              The path of the tag
     * @param renderContextMenu The runnable to render the context menu
     * @param renderChildren    The runnable to render the children
     * @param colorProvider     The provider for the color of the tag
     * @param searchProvider    The provider for the search
     */
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

    /**
     * Render a leaf in the tree.<br>
     * Used for primitive tags.
     *
     * @param text              The text to display
     * @param suffix            The suffix to display
     * @param path              The path of the tag
     * @param renderContextMenu The runnable to render the context menu
     * @param colorProvider     The provider for the color of the tag
     * @param searchProvider    The provider for the search
     */
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

    /**
     * Render the search overlay for the tag and handle the scrolling.
     *
     * @param searchProvider The provider for the search
     * @param path           The path of the tag
     */
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

    /**
     * Render the icon for the tag.
     *
     * @param drawer The drawer instance
     * @param type   The type of the icon
     */
    default void renderIcon(final ImNbtDrawer drawer, final NbtType type) {
        ImVec2 xy = ImGui.getItemRectMin();
        xy.x += ImGui.getFontSize();
        if (xy.y >= 0 && xy.y <= ImGui.getIO().getDisplaySizeY()) NbtTreeRenderer.renderIcon(drawer, xy, type);
    }

}
