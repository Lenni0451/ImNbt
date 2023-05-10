package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;
import net.lenni0451.mcstructs.nbt.tags.ListTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utils to generate a unique path for a tag.
 */
public class NbtPath {

    private static final String ESCAPE = "\\";
    private static final String PATH_SEPARATOR = ".";
    private static final String INDEX_START = "[";
    private static final String INDEX_END = "]";

    /**
     * Get the new path of a simple tag.<br>
     * e.g. "root" + "node" -> "root.node"
     *
     * @param path The current path
     * @param node The node to add
     * @return The new path
     */
    public static String get(final String path, final String node) {
        return path + PATH_SEPARATOR + node.replace(ESCAPE, ESCAPE + ESCAPE).replace(PATH_SEPARATOR, ESCAPE + PATH_SEPARATOR);
    }

    /**
     * Get the new path of an array/list entry.<br>
     * e.g. "root" + 0 -> "root[0]"
     *
     * @param path  The current path
     * @param index The index to add
     * @return The new path
     */
    public static String get(final String path, final int index) {
        return path + INDEX_START + index + INDEX_END;
    }

    /**
     * Get the new path of an array/list entry with from a node.<br>
     * e.g. "root" + "node" + 0 -> "root.node[0]"
     *
     * @param path  The current path
     * @param node  The node to add
     * @param index The index to add
     * @return The new path
     */
    public static String get(final String path, final String node, final int index) {
        return get(get(path, node), index);
    }

    /**
     * Parse the string path into an {@link IPathNode} array.
     *
     * @param path The path to parse
     * @return The parsed path
     */
    public static IPathNode[] parse(final String path) {
        List<IPathNode> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        char[] chars = path.toCharArray();
        boolean escape = false;
        boolean index = false;
        for (char c : chars) {
            if (c == ESCAPE.charAt(0)) {
                escape = true;
            } else if (escape) {
                escape = false;
                currentPart.append(c);
            } else if (c == INDEX_START.charAt(0)) {
                index = true;
                parts.add(new PathNode(currentPart.toString()));
                currentPart = new StringBuilder();
            } else if (c == INDEX_END.charAt(0)) {
                parts.add(new PathIndex(Integer.parseInt(currentPart.toString())));
                currentPart = new StringBuilder();
            } else if (c == PATH_SEPARATOR.charAt(0)) {
                if (!index) parts.add(new PathNode(currentPart.toString()));
                currentPart = new StringBuilder();
                index = false;
            } else {
                currentPart.append(c);
            }
        }
        if (!index) parts.add(new PathNode(currentPart.toString()));
        return parts.toArray(new IPathNode[0]);
    }

    /**
     * Build a string path from an {@link IPathNode} array.
     *
     * @param nodes The path nodes
     * @return The string path
     */
    public static String build(final IPathNode[] nodes) {
        String out = "";
        for (int i = 0; i < nodes.length; i++) {
            IPathNode node = nodes[i];
            if (node instanceof PathNode path) {
                if (i == 0) out = path.name();
                else out = get(out, path.name());
            } else if (node instanceof PathIndex index) {
                out = get(out, index.index);
            } else {
                throw new IllegalArgumentException("Unknown node type: " + node.getClass().getName());
            }
        }
        return out;
    }

    /**
     * Split a string path into a string array.
     *
     * @param path The path to split
     * @return The split path
     */
    public static String[] split(final String path) {
        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        char[] chars = path.toCharArray();
        boolean escape = false;
        for (char c : chars) {
            if (c == ESCAPE.charAt(0)) {
                escape = true;
            } else if (escape) {
                escape = false;
                currentPart.append(c);
            } else if (c == PATH_SEPARATOR.charAt(0)) {
                parts.add(currentPart.toString());
                currentPart = new StringBuilder();
            } else {
                currentPart.append(c);
            }
        }
        parts.add(currentPart.toString());
        return parts.toArray(new String[0]);
    }

    /**
     * Get all tags with their path from an input tag.<br>
     * Only useful when passing a list or compound tag.
     *
     * @param tags The map to put the tags in
     * @param tag  The input tag (preferably a list or compound tag)
     * @param path The path of the input tag
     */
    public static void getTags(final Map<String, INbtTag> tags, final INbtTag tag, final String path) {
        tags.put(path, tag);
        switch (tag.getNbtType()) {
            case LIST -> {
                ListTag<?> list = tag.asListTag();
                for (int i = 0; i < list.size(); i++) getTags(tags, list.get(i), get(path, i));
            }
            case COMPOUND -> {
                CompoundTag compound = tag.asCompoundTag();
                for (Map.Entry<String, INbtTag> entry : compound.getValue().entrySet()) getTags(tags, entry.getValue(), get(path, entry.getKey()));
            }
        }
    }


    /**
     * A path node.
     */
    public interface IPathNode {
        String name();
    }

    /**
     * A path node with a name.
     *
     * @param name The name
     */
    public record PathNode(String name) implements IPathNode {
    }

    /**
     * A path node with an index.
     *
     * @param index The index
     */
    public record PathIndex(int index) implements IPathNode {
        @Override
        public String name() {
            return INDEX_START + this.index + INDEX_END;
        }
    }

}
