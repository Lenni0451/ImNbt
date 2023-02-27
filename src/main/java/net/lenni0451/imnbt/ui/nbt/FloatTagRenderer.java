package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.type.ImFloat;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.FloatTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;

public class FloatTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        FloatTag floatTag = (FloatTag) tag;
        this.renderLeaf(name, ": " + this.format.format(floatTag.getValue()), path, () -> {
            this.renderIcon(4);
            if (openContextMenu) {
                ContextMenu.start().edit(name, floatTag, nameEditConsumer, t -> floatTag.setValue(t.getValue())).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, colorProvider);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        FloatTag floatTag = (FloatTag) tag;
        ImFloat value = new ImFloat(floatTag.getValue());
        if (ImGui.inputFloat("Value", value)) floatTag.setValue(value.get());
    }

}
