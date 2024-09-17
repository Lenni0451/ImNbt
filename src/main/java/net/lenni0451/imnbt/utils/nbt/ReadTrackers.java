package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.mcstructs.nbt.exceptions.NbtReadException;
import net.lenni0451.mcstructs.nbt.io.NbtReadTracker;

/**
 * A collection of {@link NbtReadTracker} which are used by ImNbt.
 */
public class ReadTrackers {

    /**
     * A read tracker with no limits.
     */
    public static final NbtReadTracker UNLIMITED = new NbtReadTracker() {
        @Override
        public void pushDepth() {
        }

        @Override
        public void popDepth() {
        }

        @Override
        public void read(int bytes) {
        }
    };
    /**
     * A more strict read tracker which breaks early if something is wrong.
     */
    public static final NbtReadTracker STRICT = new NbtReadTracker() {
        @Override
        public void read(int bytes) throws NbtReadException {
            if (bytes < 0) throw new NbtReadException("Read negative bytes (maybe overflow?)");
            super.read(bytes);
        }
    };

}
