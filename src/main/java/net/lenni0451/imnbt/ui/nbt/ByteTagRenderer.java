package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ByteTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public class ByteTagRenderer extends TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(String name, @Nonnull INbtTag tag) {
        ByteTag byteTag = (ByteTag) tag;
        this.renderLeaf(name + ": " + this.format.format(byteTag.getValue()), tag.hashCode());
    }

}
