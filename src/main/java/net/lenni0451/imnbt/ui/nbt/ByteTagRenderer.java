package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ByteTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;

public class ByteTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, String path, String name, @Nonnull INbtTag tag) {
        ByteTag byteTag = (ByteTag) tag;
        this.renderLeaf(name, ": " + this.format.format(byteTag.getValue()), path, () -> {
            this.renderIcon(0);
            ContextMenu.start().edit(name, byteTag, nameEditConsumer, t -> byteTag.setValue(t.getValue())).delete(deleteListener).sNbtParser(() -> tag).render();
        }, colorProvider);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        ByteTag byteTag = (ByteTag) tag;
        int[] value = new int[]{byteTag.getValue()};
        if (ImGui.sliderInt("Value", value, Byte.MIN_VALUE, Byte.MAX_VALUE)) byteTag.setValue((byte) value[0]);
    }

}
