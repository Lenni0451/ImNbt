package net.lenni0451.imnbt.utils;

import java.util.ArrayList;
import java.util.List;

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

}
