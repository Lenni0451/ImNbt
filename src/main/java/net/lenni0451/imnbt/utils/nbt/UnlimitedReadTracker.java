package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.mcstructs.nbt.io.NbtReadTracker;

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
