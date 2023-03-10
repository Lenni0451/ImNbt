package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import imgui.type.ImString;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.LongTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;

public class LongTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();
    private final ImString longInput = new ImString(128);

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        LongTag longTag = (LongTag) tag;
        this.renderLeaf(name, ": " + this.format.format(longTag.getValue()), path, () -> {
            this.renderIcon(3);
            if (openContextMenu) {
                ContextMenu.start().edit(name, longTag, nameEditConsumer, t -> longTag.setValue(t.getValue())).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, colorProvider);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
        LongTag longTag = (LongTag) tag;
        this.longInput.set(longTag.getValue());
        if (ImGui.inputText("Value", this.longInput)) {
            try {
                longTag.setValue(Long.parseLong(this.longInput.get()));
            } catch (NumberFormatException ignored) {
            }
        }
    }

}
