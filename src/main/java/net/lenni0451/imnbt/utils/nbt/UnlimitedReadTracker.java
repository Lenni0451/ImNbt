package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.mcstructs.nbt.io.NbtReadTracker;

/**
 * A {@link NbtReadTracker} that does not limit the amount of tags that can be read.<br>
 * MCStructs contains {@link NbtReadTracker#unlimited()} which is similar but still limits the depth of a tag.
 */
public class UnlimitedReadTracker extends NbtReadTracker {

    public static final UnlimitedReadTracker INSTANCE = new UnlimitedReadTracker();


    private UnlimitedReadTracker() {
    }

    @Override
    public void pushDepth() {
    }

    @Override
    public void popDepth() {
    }

    @Override
    public void read(int bytes) {
    }

}
