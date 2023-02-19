package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.type.ImDouble;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.DoubleTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public class DoubleTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(String name, @Nonnull INbtTag tag) {
        DoubleTag doubleTag = (DoubleTag) tag;
        this.renderLeaf(name + ": " + this.format.format(doubleTag.getValue()), tag.hashCode());
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        DoubleTag doubleTag = (DoubleTag) tag;
        ImDouble value = new ImDouble(doubleTag.getValue());
        if (ImGui.inputDouble("Value", value)) doubleTag.setValue(value.get());
    }

}
