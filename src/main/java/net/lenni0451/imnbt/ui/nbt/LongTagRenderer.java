package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.type.ImString;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
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
    private final ImString longInput = new ImString(128);

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, BiConsumer<String, INbtTag> transformListener, Runnable deleteListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        LongTag longTag = (LongTag) tag;
        this.renderLeaf(name, ": " + this.format.format(longTag.getValue()), path, () -> {
            this.renderIcon(drawer, NbtType.LONG);
            if (openContextMenu) {
                ContextMenu
                        .start(drawer)
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
        this.longInput.set(longTag.getValue());
        if (ImGui.inputText("Value", this.longInput)) {
            try {
                longTag.setValue(Long.parseLong(this.longInput.get()));
            } catch (NumberFormatException ignored) {
            }
        }
    }

}
