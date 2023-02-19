package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ByteTag;

import java.text.DecimalFormat;

public class ByteTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(String name, INbtTag tag) {
        ByteTag byteTag = (ByteTag) tag;
        this.renderLeaf(name + ": " + this.format.format(byteTag.getValue()), tag.hashCode());
    }

}
