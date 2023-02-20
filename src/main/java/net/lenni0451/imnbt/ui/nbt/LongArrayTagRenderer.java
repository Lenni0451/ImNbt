package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.LongArrayTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class LongArrayTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, String path, String name, @Nonnull INbtTag tag) {
        LongArrayTag longArrayTag = (LongArrayTag) tag;
        this.renderBranch(name + " (" + longArrayTag.getLength() + ")", path, () -> {
            ContextMenu.start().edit(name, longArrayTag, nameEditConsumer, t -> {}).delete(deleteListener).render();
        }, () -> {
            for (int i = 0; i < longArrayTag.getLength(); i++) {
                this.renderLeaf(i + ": " + this.format.format(longArrayTag.get(i)) + "##" + i, path, () -> {
                    //TODO: Edit value
                });
            }
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
