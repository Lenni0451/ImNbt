package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ByteTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class ByteTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, String name, @Nonnull INbtTag tag) {
        ByteTag byteTag = (ByteTag) tag;
        this.renderLeaf(name + ": " + this.format.format(byteTag.getValue()), tag.hashCode(), () -> {
            ContextMenu.start().edit(name, byteTag, nameEditConsumer, t -> byteTag.setValue(t.getValue())).delete(deleteListener).render();
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        ByteTag byteTag = (ByteTag) tag;
        int[] value = new int[]{byteTag.getValue()};
        if (ImGui.sliderInt("Value", value, Byte.MIN_VALUE, Byte.MAX_VALUE)) byteTag.setValue((byte) value[0]);
    }

}
