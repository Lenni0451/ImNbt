package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ShortTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;

public class ShortTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        ShortTag shortTag = (ShortTag) tag;
        this.renderLeaf(name, ": " + this.format.format(shortTag.getValue()), path, () -> {
            this.renderIcon(1);
            if (openContextMenu) {
                ContextMenu.start().edit(name, shortTag, nameEditConsumer, t -> shortTag.setValue(t.getValue())).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, colorProvider);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        ShortTag shortTag = (ShortTag) tag;
        int[] value = new int[]{shortTag.getValue()};
        if (ImGui.sliderInt("Value", value, Short.MIN_VALUE, Short.MAX_VALUE)) shortTag.setValue((short) value[0]);
    }

}
