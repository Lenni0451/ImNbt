package net.lenni0451.imnbt.utils.nbt.diff;

import net.lenni0451.imnbt.utils.NbtPath;
import net.lenni0451.imnbt.utils.TriConsumer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.NbtPath.get;

public class DiffMap {

    private final Map<String, DiffType> left = new HashMap<>();
    private final Map<String, DiffType> right = new HashMap<>();

    public void addLeft(final INbtTag tag, final String path, final DiffType type) {
        this.left.put(path, type);
        this.fillParents(path, this::getLeft, this.left::put);
        this.fillChildren(tag, path, this::getLeft, this::addLeft);
    }

    public void addRight(final INbtTag tag, final String path, final DiffType type) {
        this.right.put(path, type);
        this.fillParents(path, this::getRight, this.right::put);
        this.fillChildren(tag, path, this::getRight, this::addRight);
    }

    public void addBoth(final INbtTag left, final INbtTag right, final String path, final DiffType type) {
        this.addLeft(left, path, type);
        this.addRight(right, path, type);
    }

    @Nonnull
    public DiffType getLeft(final String path) {
        return this.left.getOrDefault(path, DiffType.UNCHANGED);
    }

    @Nonnull
    public DiffType getRight(final String path) {
        return this.right.getOrDefault(path, DiffType.UNCHANGED);
    }

    @Override
    public String toString() {
        return "DiffMap{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }


    private void fillParents(final String path, final Function<String, DiffType> typeFunction, final BiConsumer<String, DiffType> typeConsumer) {
        NbtPath.IPathNode[] nodes = NbtPath.parse(path);
        List<NbtPath.IPathNode> current = new ArrayList<>();
        for (int i = 0; i < nodes.length - 1; i++) {
            current.add(nodes[i]);
            String currentPath = NbtPath.build(current.toArray(new NbtPath.IPathNode[0]));

            if (typeFunction.apply(currentPath) == DiffType.UNCHANGED) typeConsumer.accept(currentPath, DiffType.CHILD_CHANGED);
        }
    }

    private void fillChildren(final INbtTag tag, final String path, final Function<String, DiffType> typeFunction, final TriConsumer<INbtTag, String, DiffType> typeConsumer) {
        if (tag == null) return;
        DiffType parentType = typeFunction.apply(path);
        if (!DiffType.REMOVED.equals(parentType) && !DiffType.ADDED.equals(parentType)) return;

        switch (tag.getNbtType()) {
            case BYTE_ARRAY -> {
                ByteArrayTag byteArrayTag = (ByteArrayTag) tag;
                for (int i = 0; i < byteArrayTag.getLength(); i++) typeConsumer.accept(null, get(path, i), parentType);
            }
            case LIST -> {
                ListTag<?> listTag = (ListTag<?>) tag;
                for (int i = 0; i < listTag.size(); i++) typeConsumer.accept(listTag.get(i), get(path, i), parentType);
            }
            case COMPOUND -> {
                CompoundTag compoundTag = (CompoundTag) tag;
                for (String key : compoundTag.getValue().keySet()) typeConsumer.accept(compoundTag.get(key), get(path, key), parentType);
            }
            case INT_ARRAY -> {
                IntArrayTag intArrayTag = (IntArrayTag) tag;
                for (int i = 0; i < intArrayTag.getLength(); i++) typeConsumer.accept(null, get(path, i), parentType);
            }
            case LONG_ARRAY -> {
                LongArrayTag longArrayTag = (LongArrayTag) tag;
                for (int i = 0; i < longArrayTag.getLength(); i++) typeConsumer.accept(null, get(path, i), parentType);
            }
        }
    }

}
