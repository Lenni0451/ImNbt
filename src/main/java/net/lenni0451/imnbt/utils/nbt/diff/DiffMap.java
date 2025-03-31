package net.lenni0451.imnbt.utils.nbt.diff;

import net.lenni0451.imnbt.utils.TriConsumer;
import net.lenni0451.imnbt.utils.nbt.NbtPath;
import net.lenni0451.mcstructs.nbt.NbtTag;
import net.lenni0451.mcstructs.nbt.tags.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.nbt.NbtPath.get;

/**
 * A map to store the diff result.
 */
public class DiffMap {

    private final Map<String, DiffType> left = new HashMap<>();
    private final Map<String, DiffType> right = new HashMap<>();

    /**
     * Add a node to the left side and fill the parents and children.
     *
     * @param tag  The tag to add
     * @param path The path of the tag
     * @param type The type of the tag
     */
    public void addLeft(final NbtTag tag, final String path, final DiffType type) {
        this.left.put(path, type);
        this.fillParents(path, this::getLeft, this.left::put);
        this.fillChildren(tag, path, this::getLeft, this::addLeft);
    }

    /**
     * Add a node to the right side and fill the parents and children.
     *
     * @param tag  The tag to add
     * @param path The path of the tag
     * @param type The type of the tag
     */
    public void addRight(final NbtTag tag, final String path, final DiffType type) {
        this.right.put(path, type);
        this.fillParents(path, this::getRight, this.right::put);
        this.fillChildren(tag, path, this::getRight, this::addRight);
    }

    /**
     * Add a node to both sides and fill the parents and children.
     *
     * @param left  The tag to add to the left side
     * @param right The tag to add to the right side
     * @param path  The path of the tags
     * @param type  The type of the tags
     */
    public void addBoth(final NbtTag left, final NbtTag right, final String path, final DiffType type) {
        this.addLeft(left, path, type);
        this.addRight(right, path, type);
    }

    /**
     * Get the diff type of a node on the left side.
     *
     * @param path The path of the node
     * @return The diff type
     */
    @Nonnull
    public DiffType getLeft(final String path) {
        return this.left.getOrDefault(path, DiffType.UNCHANGED);
    }

    /**
     * Get the diff type of a node on the right side.
     *
     * @param path The path of the node
     * @return The diff type
     */
    @Nonnull
    public DiffType getRight(final String path) {
        return this.right.getOrDefault(path, DiffType.UNCHANGED);
    }

    @Override
    public String toString() {
        return "DiffMap{" +
                "left=" + this.left +
                ", right=" + this.right +
                '}';
    }


    /**
     * Fill all parents of a node by parsing the path and building it piece by piece.
     *
     * @param path         The path of the node
     * @param typeFunction The function to get the type of a node
     * @param typeConsumer The consumer to add the node to the map
     */
    private void fillParents(final String path, final Function<String, DiffType> typeFunction, final BiConsumer<String, DiffType> typeConsumer) {
        NbtPath.IPathNode[] nodes = NbtPath.parse(path);
        List<NbtPath.IPathNode> current = new ArrayList<>();
        for (int i = 0; i < nodes.length - 1; i++) {
            current.add(nodes[i]);
            String currentPath = NbtPath.build(current.toArray(new NbtPath.IPathNode[0]));

            if (typeFunction.apply(currentPath) == DiffType.UNCHANGED) {
                //The tag should only get the CHILD_CHANGED type if it is not already changed itself
                typeConsumer.accept(currentPath, DiffType.CHILD_CHANGED);
            }
        }
    }

    /**
     * Go through all children of a node and give them the same type as the parent.
     *
     * @param tag          The tag to go through
     * @param path         The path of the tag
     * @param typeFunction The function to get the type of a node
     * @param typeConsumer The consumer to add the node to the map
     */
    private void fillChildren(final NbtTag tag, final String path, final Function<String, DiffType> typeFunction, final TriConsumer<NbtTag, String, DiffType> typeConsumer) {
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
