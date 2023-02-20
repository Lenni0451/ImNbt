package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ByteArrayTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class ByteArrayTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, String name, @Nonnull INbtTag tag) {
        ByteArrayTag byteArrayTag = (ByteArrayTag) tag;
        this.renderBranch(name + " (" + byteArrayTag.getLength() + ")", tag.hashCode(), () -> {
            ContextMenu.start().edit(name, byteArrayTag, nameEditConsumer, t -> {}).render();

            for (int i = 0; i < byteArrayTag.getLength(); i++) {
                this.renderLeaf(i + ": " + this.format.format(byteArrayTag.get(i)) + "##" + i, tag.hashCode(), () -> {
                    //TODO: Edit value
                });
            }
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
