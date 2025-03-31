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
import net.lenni0451.mcstructs.nbt.tags.FloatTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The renderer for float tags.
 */
public class FloatTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();
    private final ImGuiNumberPicker numberPicker = new ImGuiNumberPicker(float.class);

    public FloatTagRenderer() {
        this.format.setMaximumFractionDigits(Float.MAX_EXPONENT);
    }

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, BiConsumer<String, NbtTag> transformListener, Runnable deleteListener, Runnable modificationListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull NbtTag tag) {
        FloatTag floatTag = (FloatTag) tag;
        this.renderLeaf(name, ": " + this.format.format(floatTag.getValue()), path, () -> {
            this.renderIcon(drawer, NbtType.FLOAT);
            if (openContextMenu) {
                ContextMenu
                        .start(drawer, modificationListener)
                        .transform(TagTransformer.transform(drawer, name, floatTag, transformListener), TagTransformer.FLOAT_TRANSFORMS)
                        .round(decimalPlaces -> floatTag.setValue(NumberUtils.round(floatTag.getValue(), decimalPlaces)))
                        .edit(name, floatTag, nameEditConsumer, t -> {
                            floatTag.setValue(t.getValue());
                            searchProvider.refreshSearch();
                        })
                        .copy(name, floatTag)
                        .delete(deleteListener)
                        .sNbtParser(() -> tag)
                        .render();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    @Override
    public void renderValueEditor(NbtTag tag) {
        FloatTag floatTag = (FloatTag) tag;
        floatTag.setValue(this.numberPicker.render(floatTag.getValue()).floatValue());
    }

}
