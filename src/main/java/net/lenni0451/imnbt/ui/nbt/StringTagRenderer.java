package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.type.ImString;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.StringTag;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Function;

public class StringTagRenderer implements TagRenderer {

    private final ImString valueEditor = new ImString(32767);

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        StringTag stringTag = (StringTag) tag;
        this.renderLeaf(name, ": " + stringTag.getValue(), path, () -> {
            this.renderIcon(7);
            if (openContextMenu) {
                ContextMenu.start().edit(name, stringTag, nameEditConsumer, t -> {
                    stringTag.setValue(t.getValue());
                    searchProvider.refreshSearch();
                }).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        StringTag stringTag = (StringTag) tag;
        this.valueEditor.set(stringTag.getValue());
        if (ImGui.inputText("Value", this.valueEditor)) stringTag.setValue(this.valueEditor.get());
    }

}
