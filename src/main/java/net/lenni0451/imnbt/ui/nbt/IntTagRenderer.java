package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.type.ImInt;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.IntTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;

public class IntTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        IntTag intTag = (IntTag) tag;
        this.renderLeaf(name, ": " + this.format.format(intTag.getValue()), path, () -> {
            this.renderIcon(2);
            if (openContextMenu) {
                ContextMenu.start().edit(name, intTag, nameEditConsumer, t -> {
                    intTag.setValue(t.getValue());
                    searchProvider.refreshSearch();
                }).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        IntTag intTag = (IntTag) tag;
        ImInt value = new ImInt(intTag.getValue());
        if (ImGui.inputInt("Value", value)) intTag.setValue(value.get());
    }

}
