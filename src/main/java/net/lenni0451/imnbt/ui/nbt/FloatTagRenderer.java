package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.type.ImFloat;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.FloatTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The renderer for float tags.
 */
public class FloatTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    public FloatTagRenderer() {
        this.format.setMaximumFractionDigits(Float.MAX_EXPONENT);
    }

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        FloatTag floatTag = (FloatTag) tag;
        this.renderLeaf(name, ": " + this.format.format(floatTag.getValue()), path, () -> {
            this.renderIcon(drawer, 4);
            if (openContextMenu) {
                ContextMenu.start(drawer).edit(name, floatTag, nameEditConsumer, t -> {
                    floatTag.setValue(t.getValue());
                    searchProvider.refreshSearch();
                }).copy(floatTag).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        FloatTag floatTag = (FloatTag) tag;
        ImFloat value = new ImFloat(floatTag.getValue());
        if (ImGui.inputFloat("Value", value)) floatTag.setValue(value.get());
    }

}
