package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ShortTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The renderer for short tags.
 */
public class ShortTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        ShortTag shortTag = (ShortTag) tag;
        this.renderLeaf(name, ": " + this.format.format(shortTag.getValue()), path, () -> {
            this.renderIcon(drawer, 1);
            if (openContextMenu) {
                ContextMenu.start(drawer).edit(name, shortTag, nameEditConsumer, t -> {
                    shortTag.setValue(t.getValue());
                    searchProvider.refreshSearch();
                }).copy(name, shortTag).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        ShortTag shortTag = (ShortTag) tag;
        int[] value = new int[]{shortTag.getValue()};
        if (ImGui.sliderInt("Value", value, Short.MIN_VALUE, Short.MAX_VALUE)) shortTag.setValue((short) value[0]);
    }

}
