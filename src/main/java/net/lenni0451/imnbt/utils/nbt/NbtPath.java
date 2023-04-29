package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;
import net.lenni0451.mcstructs.nbt.tags.ListTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NbtPath {

    private static final String ESCAPE = "\\";
    private static final String PATH_SEPARATOR = ".";
    private static final String INDEX_START = "[";
    private static final String INDEX_END = "]";

    public static String get(final String path, final String node) {
        return path + PATH_SEPARATOR + node.replace(ESCAPE, ESCAPE + ESCAPE).replace(PATH_SEPARATOR, ESCAPE + PATH_SEPARATOR);
    }

    public static String get(final String path, final int index) {
        return path + INDEX_START + index + INDEX_END;
    }

    public static String get(final String path, final String node, final int index) {
        return get(get(path, node), index);
    }

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

    public static Map<String, INbtTag> getTags(final INbtTag tag, final String path) {
        Map<String, INbtTag> tags = new HashMap<>();
        tags.put(path, tag);
        switch (tag.getNbtType()) {
            case LIST -> {
                ListTag<?> list = tag.asListTag();
                for (int i = 0; i < list.size(); i++) tags.putAll(getTags(list.get(i), get(path, i)));
            }
            case COMPOUND -> {
                CompoundTag compound = tag.asCompoundTag();
                for (Map.Entry<String, INbtTag> entry : compound.getValue().entrySet()) tags.putAll(getTags(entry.getValue(), get(path, entry.getKey())));
            }
        }
        return tags;
    }


    public interface IPathNode {
        String name();
    }

    public record PathNode(String name) implements IPathNode {
    }

    public record PathIndex(int index) implements IPathNode {
        @Override
        public String name() {
            return INDEX_START + this.index + INDEX_END;
        }
    }

}
