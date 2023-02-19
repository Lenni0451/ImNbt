package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.IntTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public class IntTagRenderer extends TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(String name, @Nonnull INbtTag tag) {
        IntTag intTag = (IntTag) tag;
        this.renderLeaf(name + ": " + this.format.format(intTag.getValue()), tag.hashCode());
    }

}
