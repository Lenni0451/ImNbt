package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.imnbt.utils.nbt.TagTransformer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.ByteTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The renderer for byte tags.
 */
public class ByteTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, BiConsumer<String, INbtTag> transformListener, Runnable deleteListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        ByteTag byteTag = (ByteTag) tag;
        this.renderLeaf(name, ": " + this.format.format(byteTag.getValue()), path, () -> {
            this.renderIcon(drawer, NbtType.BYTE);
            if (openContextMenu) {
                ContextMenu
                        .start(drawer)
                        .transform(TagTransformer.transform(drawer, name, byteTag, transformListener), TagTransformer.BYTE_TRANSFORMS)
                        .edit(name, byteTag, nameEditConsumer, t -> {
                            byteTag.setValue(t.getValue());
                            searchProvider.refreshSearch();
                        })
                        .copy(name, byteTag)
                        .delete(deleteListener)
                        .sNbtParser(() -> tag)
                        .render();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        ByteTag byteTag = (ByteTag) tag;
        int[] value = new int[]{byteTag.getValue()};
        if (ImGui.sliderInt("Value", value, Byte.MIN_VALUE, Byte.MAX_VALUE)) byteTag.setValue((byte) value[0]);
    }

}
