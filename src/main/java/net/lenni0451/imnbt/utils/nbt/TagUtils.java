package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;
import net.lenni0451.mcstructs.nbt.tags.ListTag;

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

    /**
     * Count the total amount of tags in a tag.
     *
     * @param tag The tag to count
     * @return The amount of tags
     */
    public static int size(final INbtTag tag) {
        if (tag == null) return 0;
        return switch (tag.getNbtType()) {
            case END -> 0;
            case BYTE, STRING, DOUBLE, FLOAT, LONG, INT, SHORT -> 1;
            case BYTE_ARRAY -> tag.asByteArrayTag().getLength() + 1;
            case LIST -> {
                ListTag<?> listTag = tag.asListTag();
                int size = 1;
                for (INbtTag listElement : listTag) {
                    size += size(listElement);
                }
                yield size;
            }
            case COMPOUND -> {
                CompoundTag compoundTag = tag.asCompoundTag();
                int size = 1;
                for (INbtTag compoundElement : compoundTag.getValue().values()) {
                    size += size(compoundElement);
                }
                yield size;
            }
            case INT_ARRAY -> tag.asIntArrayTag().getLength() + 1;
            case LONG_ARRAY -> tag.asLongArrayTag().getLength() + 1;
        };
    }

}
