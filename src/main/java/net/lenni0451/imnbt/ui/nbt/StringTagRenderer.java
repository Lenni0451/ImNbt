package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.type.ImString;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.StringTag;

import javax.annotation.Nonnull;

public class StringTagRenderer implements TagRenderer {

    private final ImString valueEditor = new ImString(32767);

    @Override
    public void render(String name, @Nonnull INbtTag tag) {
        StringTag stringTag = (StringTag) tag;
        this.renderLeaf(name + ": " + stringTag.getValue(), tag.hashCode());
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        StringTag stringTag = (StringTag) tag;
        this.valueEditor.set(stringTag.getValue());
        if (ImGui.inputText("Value", this.valueEditor)) stringTag.setValue(this.valueEditor.get());
    }

}
