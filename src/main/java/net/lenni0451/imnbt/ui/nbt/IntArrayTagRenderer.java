package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.IntArrayTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class IntArrayTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, String path, String name, @Nonnull INbtTag tag) {
        IntArrayTag intArrayTag = (IntArrayTag) tag;
        this.renderBranch(name + " (" + intArrayTag.getLength() + ")", path, () -> {
            ContextMenu.start().edit(name, intArrayTag, nameEditConsumer, t -> {}).delete(deleteListener).render();
        }, () -> {
            for (int i = 0; i < intArrayTag.getLength(); i++) {
                this.renderLeaf(i + ": " + this.format.format(intArrayTag.get(i)) + "##" + i, path, () -> {
                    //TODO: Edit value
                });
            }
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
