package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ByteArrayTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public class ByteArrayTagRenderer extends TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(String name, @Nonnull INbtTag tag) {
        ByteArrayTag byteArrayTag = (ByteArrayTag) tag;
        this.renderBranch(name + " (" + byteArrayTag.getLength() + ")", tag.hashCode(), () -> {
            for (int i = 0; i < byteArrayTag.getLength(); i++) this.renderLeaf(i + ": " + this.format.format(byteArrayTag.get(i)) + "##" + i, tag.hashCode());
        });
    }

}
