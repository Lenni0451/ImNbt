package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.imnbt.utils.ImGuiNumberPicker;
import net.lenni0451.imnbt.utils.nbt.TagTransformer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.ShortTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The renderer for short tags.
 */
public class ShortTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();
    private final ImGuiNumberPicker numberPicker = new ImGuiNumberPicker(short.class);

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, BiConsumer<String, INbtTag> transformListener, Runnable deleteListener, Runnable modificationListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        ShortTag shortTag = (ShortTag) tag;
        this.renderLeaf(name, ": " + this.format.format(shortTag.getValue()), path, () -> {
            this.renderIcon(drawer, NbtType.SHORT);
            if (openContextMenu) {
                ContextMenu
                        .start(drawer, modificationListener)
                        .transform(TagTransformer.transform(drawer, name, shortTag, transformListener), TagTransformer.SHORT_TRANSFORMS)
                        .edit(name, shortTag, nameEditConsumer, t -> {
                            shortTag.setValue(t.getValue());
                            searchProvider.refreshSearch();
                        })
                        .copy(name, shortTag)
                        .delete(deleteListener)
                        .sNbtParser(() -> tag)
                        .render();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        ShortTag shortTag = (ShortTag) tag;
        shortTag.setValue(this.numberPicker.render(shortTag.getValue()).shortValue());
    }

}
