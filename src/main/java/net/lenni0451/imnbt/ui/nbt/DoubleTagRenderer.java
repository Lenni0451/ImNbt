package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.type.ImDouble;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.DoubleTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The renderer for double tags.
 */
public class DoubleTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    public DoubleTagRenderer() {
        this.format.setMaximumFractionDigits(Double.MAX_EXPONENT);
    }

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        DoubleTag doubleTag = (DoubleTag) tag;
        this.renderLeaf(name, ": " + this.format.format(doubleTag.getValue()), path, () -> {
            this.renderIcon(drawer, 5);
            if (openContextMenu) {
                ContextMenu.start(drawer).edit(name, doubleTag, nameEditConsumer, t -> {
                    doubleTag.setValue(t.getValue());
                    searchProvider.refreshSearch();
                }).copy(doubleTag).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        DoubleTag doubleTag = (DoubleTag) tag;
        ImDouble value = new ImDouble(doubleTag.getValue());
        if (ImGui.inputDouble("Value", value)) doubleTag.setValue(value.get());
    }

}
