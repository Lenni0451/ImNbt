package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.type.ImDouble;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.DoubleTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class DoubleTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, String name, @Nonnull INbtTag tag) {
        DoubleTag doubleTag = (DoubleTag) tag;
        this.renderLeaf(name + ": " + this.format.format(doubleTag.getValue()), tag.hashCode(), () -> {
            ContextMenu.start().edit(name, doubleTag, nameEditConsumer, t -> doubleTag.setValue(t.getValue())).render();
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        DoubleTag doubleTag = (DoubleTag) tag;
        ImDouble value = new ImDouble(doubleTag.getValue());
        if (ImGui.inputDouble("Value", value)) doubleTag.setValue(value.get());
    }

}
