package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.StringTag;

import javax.annotation.Nonnull;

public class StringTagRenderer implements TagRenderer {

    @Override
    public void render(String name, @Nonnull INbtTag tag) {
        StringTag stringTag = (StringTag) tag;
        this.renderLeaf(name + ": " + stringTag.getValue(), tag.hashCode());
    }

}
