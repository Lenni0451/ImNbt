package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.LongTag;

import java.text.DecimalFormat;

public class LongTagRenderer extends TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(String name, INbtTag tag) {
        LongTag longTag = (LongTag) tag;
        this.renderLeaf(name + ": " + this.format.format(longTag.getValue()), tag.hashCode());
    }

}
