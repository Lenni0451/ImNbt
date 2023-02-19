package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.IntTag;

import java.text.DecimalFormat;

public class IntTagRenderer extends TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(String name, INbtTag tag) {
        IntTag intTag = (IntTag) tag;
        this.renderLeaf(name + ": " + this.format.format(intTag.getValue()), tag.hashCode());
    }

}
