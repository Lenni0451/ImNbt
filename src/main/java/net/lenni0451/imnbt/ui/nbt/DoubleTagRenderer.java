package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.imnbt.utils.ImGuiNumberPicker;
import net.lenni0451.imnbt.utils.NumberUtils;
import net.lenni0451.imnbt.utils.nbt.TagTransformer;
import net.lenni0451.mcstructs.nbt.NbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.DoubleTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The renderer for double tags.
 */
public class DoubleTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();
    private final ImGuiNumberPicker numberPicker = new ImGuiNumberPicker(double.class);

    public DoubleTagRenderer() {
        this.format.setMaximumFractionDigits(Double.MAX_EXPONENT);
    }

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, BiConsumer<String, NbtTag> transformListener, Runnable deleteListener, Runnable modificationListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull NbtTag tag) {
        DoubleTag doubleTag = (DoubleTag) tag;
        this.renderLeaf(name, ": " + this.format.format(doubleTag.getValue()), path, () -> {
            this.renderIcon(drawer, NbtType.DOUBLE);
            if (openContextMenu) {
                ContextMenu
                        .start(drawer, modificationListener)
                        .transform(TagTransformer.transform(drawer, name, doubleTag, transformListener), TagTransformer.DOUBLE_TRANSFORMS)
                        .round(decimalPlaces -> doubleTag.setValue(NumberUtils.round(doubleTag.getValue(), decimalPlaces)))
                        .edit(name, doubleTag, nameEditConsumer, t -> {
                            doubleTag.setValue(t.getValue());
                            searchProvider.refreshSearch();
                        })
                        .copy(name, doubleTag)
                        .delete(deleteListener)
                        .sNbtParser(() -> tag)
                        .render();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    @Override
    public void renderValueEditor(NbtTag tag) {
        DoubleTag doubleTag = (DoubleTag) tag;
        doubleTag.setValue(this.numberPicker.render(doubleTag.getValue()).doubleValue());
    }

}
