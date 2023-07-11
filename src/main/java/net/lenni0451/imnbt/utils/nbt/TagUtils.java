package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

public class TagUtils {

    /**
     * Find a new unique name for a tag in a compound.
     *
     * @param compound The compound to search in
     * @param name     The name to start with
     * @return The new unique name
     */
    public static String findUniqueName(final CompoundTag compound, final String name) {
        String newName = name;
        for (int i = 1; compound.contains(newName); i++) {
            if (name.isEmpty()) newName = String.valueOf(i);
            else newName = name + "_" + i;
        }
        return newName;
    }

}
