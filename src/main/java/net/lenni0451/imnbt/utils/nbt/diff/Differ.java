package net.lenni0451.imnbt.utils.nbt.diff;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.*;

import static net.lenni0451.imnbt.utils.NbtPath.get;

public class Differ {

    public static DiffMap diff(final INbtTag left, final INbtTag right) {
        DiffMap map = new DiffMap();
        diff(map, "", left, right);
        return map;
    }

    private static void diff(final DiffMap map, final String path, final INbtTag left, final INbtTag right) {
        if (left == null && right == null) {
            return;
        } else if (left == null) {
            map.addRight(right, path, DiffType.ADDED);
            return;
        } else if (right == null) {
            map.addLeft(left, path, DiffType.REMOVED);
            return;
        }
        if (!left.getNbtType().equals(right.getNbtType())) {
            map.addBoth(left, right, path, DiffType.TYPE_CHANGED);
            return;
        }

        switch (left.getNbtType()) {
            case BYTE_ARRAY -> {
                ByteArrayTag leftArray = (ByteArrayTag) left;
                ByteArrayTag rightArray = (ByteArrayTag) right;
                int count = Math.max(leftArray.getLength(), rightArray.getLength());
                for (int i = 0; i < count; i++) {
                    Byte l = i < leftArray.getLength() ? leftArray.get(i) : null;
                    Byte r = i < rightArray.getLength() ? rightArray.get(i) : null;

                    if (l == null) map.addRight(null, get(path, i), DiffType.ADDED);
                    else if (r == null) map.addLeft(null, get(path, i), DiffType.REMOVED);
                    else if (!l.equals(r)) map.addBoth(null, null, get(path, i), DiffType.VALUE_CHANGED);
                }
            }
            case LIST -> {
                ListTag<?> leftList = (ListTag<?>) left;
                ListTag<?> rightList = (ListTag<?>) right;
                int count = Math.max(leftList.size(), rightList.size());
                for (int i = 0; i < count; i++) {
                    INbtTag l = i < leftList.size() ? leftList.get(i) : null;
                    INbtTag r = i < rightList.size() ? rightList.get(i) : null;

                    if (l == null) map.addRight(r, get(path, i), DiffType.ADDED);
                    else if (r == null) map.addLeft(l, get(path, i), DiffType.REMOVED);
                    else diff(map, get(path, i), l, r);
                }
            }
            case COMPOUND -> {
                CompoundTag leftCompound = (CompoundTag) left;
                CompoundTag rightCompound = (CompoundTag) right;
                for (String key : leftCompound.getValue().keySet()) {
                    if (!rightCompound.contains(key)) map.addLeft(leftCompound.get(key), get(path, key), DiffType.REMOVED);
                    else diff(map, path + "." + key, leftCompound.get(key), rightCompound.get(key));
                }
                for (String key : rightCompound.getValue().keySet()) {
                    if (!leftCompound.contains(key)) map.addRight(rightCompound.get(key), get(path, key), DiffType.ADDED);
                }
            }
            case INT_ARRAY -> {
                IntArrayTag leftArray = (IntArrayTag) left;
                IntArrayTag rightArray = (IntArrayTag) right;
                int count = Math.max(leftArray.getLength(), rightArray.getLength());
                for (int i = 0; i < count; i++) {
                    Integer l = i < leftArray.getLength() ? leftArray.get(i) : null;
                    Integer r = i < rightArray.getLength() ? rightArray.get(i) : null;

                    if (l == null) map.addRight(null, get(path, i), DiffType.ADDED);
                    else if (r == null) map.addLeft(null, get(path, i), DiffType.REMOVED);
                    else if (!l.equals(r)) map.addBoth(null, null, get(path, i), DiffType.VALUE_CHANGED);
                }
            }
            case LONG_ARRAY -> {
                LongArrayTag leftArray = (LongArrayTag) left;
                LongArrayTag rightArray = (LongArrayTag) right;
                int count = Math.max(leftArray.getLength(), rightArray.getLength());
                for (int i = 0; i < count; i++) {
                    Long l = i < leftArray.getLength() ? leftArray.get(i) : null;
                    Long r = i < rightArray.getLength() ? rightArray.get(i) : null;

                    if (l == null) map.addRight(null, get(path, i), DiffType.ADDED);
                    else if (r == null) map.addLeft(null, get(path, i), DiffType.REMOVED);
                    else if (!l.equals(r)) map.addBoth(null, null, get(path, i), DiffType.VALUE_CHANGED);
                }
            }
            default -> {
                if (!left.equals(right)) map.addBoth(left, right, path, DiffType.VALUE_CHANGED);
            }
        }
    }

}
