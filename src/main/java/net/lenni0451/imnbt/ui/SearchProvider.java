package net.lenni0451.imnbt.ui;

import net.lenni0451.imnbt.utils.nbt.NbtPath;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.StringTag;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SearchProvider {

    private final Set<String> searchPaths = new LinkedHashSet<>();
    private final Set<String> expandPaths = new LinkedHashSet<>();
    private String search = "";
    private boolean doScroll = false;

    public void setSearch(final String search) {
        this.search = search;
    }

    public void buildSearchPaths(@Nullable final INbtTag tag) {
        if (this.search.isEmpty() || tag == null) {
            this.searchPaths.clear();
            this.expandPaths.clear();
            return;
        }

        this.searchPaths.clear();
        this.expandPaths.clear();
        Map<String, INbtTag> tags = NbtPath.getTags(tag, "");
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
        if (this.search.isEmpty()) return false;
        return this.searchPaths.contains(path);
    }

    public boolean isExpanded(final String path) {
        if (this.search.isEmpty()) return false;
        return this.expandPaths.contains(path);
    }

    public void setDoScroll(final boolean doScroll) {
        this.doScroll = doScroll;
    }

    public boolean shouldDoScroll() {
        if (this.doScroll) {
            this.doScroll = false;
            return true;
        }
        return false;
    }
}
