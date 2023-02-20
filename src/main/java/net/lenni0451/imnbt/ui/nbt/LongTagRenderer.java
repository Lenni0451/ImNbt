package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.LongTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class LongTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();
    private final ImString longInput = new ImString(128);

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, String path, String name, @Nonnull INbtTag tag) {
        LongTag longTag = (LongTag) tag;
        this.renderLeaf(name + ": " + this.format.format(longTag.getValue()), path, () -> {
            ContextMenu.start().edit(name, longTag, nameEditConsumer, t -> longTag.setValue(t.getValue())).delete(deleteListener).render();
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        LongTag longTag = (LongTag) tag;
        this.longInput.set(this.format.format(longTag.getValue()));
        if (ImGui.inputText("Value", this.longInput, ImGuiInputTextFlags.EnterReturnsTrue)) {
            try {
                longTag.setValue(Long.parseLong(this.longInput.get()));
            } catch (NumberFormatException ignored) {
            }
        }
    }

}
