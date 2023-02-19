package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.StringTag;

public class StringTagRenderer extends TagRenderer {

    @Override
    public void render(String name, INbtTag tag) {
        StringTag stringTag = (StringTag) tag;
        this.renderLeaf(name + ": " + stringTag.getValue(), tag.hashCode());
    }

}
