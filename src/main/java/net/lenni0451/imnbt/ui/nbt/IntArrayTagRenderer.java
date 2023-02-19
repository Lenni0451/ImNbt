package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.IntArrayTag;

import java.text.DecimalFormat;

public class IntArrayTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(String name, INbtTag tag) {
        IntArrayTag intArrayTag = (IntArrayTag) tag;
        this.renderBranch(name + " (" + intArrayTag.getLength() + ")", tag.hashCode(), () -> {
            for (int i = 0; i < intArrayTag.getLength(); i++) this.renderLeaf(i + ": " + this.format.format(intArrayTag.get(i)) + "##" + i, tag.hashCode());
        });
    }

}
