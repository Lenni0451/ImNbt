package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.FloatTag;

import java.text.DecimalFormat;

public class FloatTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(String name, INbtTag tag) {
        FloatTag floatTag = (FloatTag) tag;
        this.renderLeaf(name + ": " + this.format.format(floatTag.getValue()), tag.hashCode());
    }

}
