package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.imnbt.utils.CollectionUtils;
import net.lenni0451.mcstructs.nbt.NbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;
import net.lenni0451.mcstructs.nbt.tags.ListTag;

import java.util.Comparator;
import java.util.Map;

public class TagSorter {

    public static void sort(final NbtTag tag) {
        if (tag instanceof ListTag<?> list) {
            if (list.getType() == null) return;

            if (NbtType.STRING.equals(list.getType())) {
                list.getValue().sort((Comparator<NbtTag>) (o1, o2) -> o1.asStringTag().getValue().compareToIgnoreCase(o2.asStringTag().getValue()));
            } else if (list.getType().isNumber()) {
                list.getValue().sort(Comparator.comparingDouble((NbtTag o) -> o.asNumberTag().doubleValue()));
            }
        } else if (tag instanceof CompoundTag compound) {
            Map<String, NbtTag> entries = compound.getValue();
            compound.setValue(CollectionUtils.sort(entries, Map.Entry.comparingByKey(String::compareToIgnoreCase)));
        }
    }

}
