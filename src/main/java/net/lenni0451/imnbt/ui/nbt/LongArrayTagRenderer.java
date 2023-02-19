package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.LongArrayTag;

import java.text.DecimalFormat;

public class LongArrayTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(String name, INbtTag tag) {
        LongArrayTag longArrayTag = (LongArrayTag) tag;
        this.renderBranch(name + " (" + longArrayTag.getLength() + ")", tag.hashCode(), () -> {
            for (int i = 0; i < longArrayTag.getLength(); i++) this.renderLeaf(i + ": " + this.format.format(longArrayTag.get(i)) + "##" + i, tag.hashCode());
        });
    }

}
