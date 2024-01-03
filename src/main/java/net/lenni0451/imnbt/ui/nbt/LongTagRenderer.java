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
import net.lenni0451.mcstructs.nbt.tags.LongTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The renderer for long tags.
 */
public class LongTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();
    private final ImGuiNumberPicker numberPicker = new ImGuiNumberPicker(long.class);

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, BiConsumer<String, INbtTag> transformListener, Runnable deleteListener, Runnable modificationListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        LongTag longTag = (LongTag) tag;
        this.renderLeaf(name, ": " + this.format.format(longTag.getValue()), path, () -> {
            this.renderIcon(drawer, NbtType.LONG);
            if (openContextMenu) {
                ContextMenu
                        .start(drawer, modificationListener)
                        .transform(TagTransformer.transform(drawer, name, longTag, transformListener), TagTransformer.LONG_TRANSFORMS)
                        .edit(name, longTag, nameEditConsumer, t -> {
                            longTag.setValue(t.getValue());
                            searchProvider.refreshSearch();
                        })
                        .copy(name, longTag)
                        .delete(deleteListener)
                        .sNbtParser(() -> tag)
                        .render();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        LongTag longTag = (LongTag) tag;
        longTag.setValue(this.numberPicker.render(longTag.getValue()).longValue());
    }

}
