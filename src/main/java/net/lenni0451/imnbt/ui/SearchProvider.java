package net.lenni0451.imnbt.ui;

import net.lenni0451.imnbt.utils.nbt.NbtPath;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.StringTag;

import javax.annotation.Nullable;
import java.util.*;

public class SearchProvider {

    private final List<String> searchPaths = new ArrayList<>();
    private final Set<String> expandPaths = new LinkedHashSet<>();
    private String search = "";
    private int currentScrollIndex = -1;
    private boolean doScroll = false;

    public void setSearch(final String search) {
        this.search = search;
    }

    public void buildSearchPaths(@Nullable final INbtTag tag) {
        this.searchPaths.clear();
        this.expandPaths.clear();
        this.currentScrollIndex = -1;
        this.doScroll = false;
        if (this.search.isEmpty() || tag == null) return;

        Map<String, INbtTag> tags = new LinkedHashMap<>();
        NbtPath.getTags(tags, tag, "");
        for (Map.Entry<String, INbtTag> entry : tags.entrySet()) {
            NbtPath.IPathNode[] paths = NbtPath.parse(entry.getKey());
            String name = paths[paths.length - 1].name();
            if (name.toLowerCase().contains(this.search.toLowerCase())) {
                this.searchPaths.add(entry.getKey());
                this.expandParents(paths);
            } else {
                switch (entry.getValue().getNbtType()) {
                    case BYTE -> {

                    }
                    case SHORT -> {

                    }
                    case INT -> {

                    }
                    case LONG -> {

                    }
                    case FLOAT -> {

                    }
                    case DOUBLE -> {

                    }
                    case BYTE_ARRAY -> {

                    }
                    case STRING -> {
                        StringTag string = (StringTag) entry.getValue();
                        if (string.getValue().toLowerCase().contains(this.search.toLowerCase())) {
                            this.searchPaths.add(entry.getKey());
                            this.expandParents(paths);
                        }
                    }
                    case INT_ARRAY -> {

                    }
                    case LONG_ARRAY -> {

                    }
                }
            }
        }
    }

    private void expandParents(final NbtPath.IPathNode[] paths) {
        String path = null;
        for (int i = 0; i < paths.length - 1; i++) {
            NbtPath.IPathNode node = paths[i];
            if (path == null) {
                path = node.name();
            } else {
                if (node instanceof NbtPath.PathNode pathNode) path = NbtPath.get(path, pathNode.name());
                else if (node instanceof NbtPath.PathIndex pathIndex) path = NbtPath.get(path, pathIndex.index());
                else throw new IllegalStateException("Unknown path node type: " + node.getClass().getName());
            }
            this.expandPaths.add(path);
        }
    }

    public boolean isTargeted(final String path) {
        if (this.search.isEmpty() || this.searchPaths.isEmpty()) return false;
        return this.searchPaths.contains(path);
    }

    public boolean isExpanded(final String path) {
        if (this.search.isEmpty() || this.searchPaths.isEmpty()) return false;
        return this.expandPaths.contains(path);
    }

    public void setDoScroll(final SearchDirection direction) {
        if (this.search.isEmpty() || this.searchPaths.isEmpty()) return;
        this.doScroll = true;
        this.currentScrollIndex += switch (direction) {
            case BACK -> -1;
            case NEXT -> 1;
        };
        this.currentScrollIndex %= this.searchPaths.size();
        if (this.currentScrollIndex < 0) this.currentScrollIndex = this.searchPaths.size() - 1;
    }

    public boolean shouldDoScroll(final String path) {
        if (this.search.isEmpty() || this.searchPaths.isEmpty()) return false;
        if (this.doScroll && this.searchPaths.get(this.currentScrollIndex).equals(path)) {
            this.doScroll = false;
            return true;
        }
        return false;
    }


    public enum SearchDirection {
        BACK, NEXT
    }

}
