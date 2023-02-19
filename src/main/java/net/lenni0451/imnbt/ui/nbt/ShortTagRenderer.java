package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ShortTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public class ShortTagRenderer extends TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(String name, @Nonnull INbtTag tag) {
        ShortTag shortTag = (ShortTag) tag;
        this.renderLeaf(name + ": " + this.format.format(shortTag.getValue()), tag.hashCode());
    }

}
