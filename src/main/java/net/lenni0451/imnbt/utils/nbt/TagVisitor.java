package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;
import net.lenni0451.mcstructs.nbt.tags.ListTag;

import java.util.function.Consumer;

public class TagVisitor {

    public static void visit(final INbtTag tag, final Consumer<INbtTag> visitor) {
        visitor.accept(tag);
        if (tag instanceof CompoundTag compoundTag) {
            for (INbtTag value : compoundTag.getValue().values()) visit(value, visitor);
        } else if (tag instanceof ListTag<?> listTag) {
            for (INbtTag value : listTag.getValue()) visit(value, visitor);
        }
    }

}
