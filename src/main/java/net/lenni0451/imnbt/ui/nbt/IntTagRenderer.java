package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.type.ImInt;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.IntTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class IntTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, String path, String name, @Nonnull INbtTag tag) {
        IntTag intTag = (IntTag) tag;
        this.renderLeaf(name, ": " + this.format.format(intTag.getValue()), path, () -> {
            this.renderIcon(2);
            ContextMenu.start().edit(name, intTag, nameEditConsumer, t -> intTag.setValue(t.getValue())).delete(deleteListener).sNbtParser(() -> tag).render();
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        IntTag intTag = (IntTag) tag;
        ImInt value = new ImInt(intTag.getValue());
        if (ImGui.inputInt("Value", value)) intTag.setValue(value.get());
    }

}
