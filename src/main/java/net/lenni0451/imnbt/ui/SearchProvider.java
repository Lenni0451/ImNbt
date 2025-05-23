package net.lenni0451.imnbt.ui;

import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.utils.NumberUtils;
import net.lenni0451.imnbt.utils.nbt.NbtPath;
import net.lenni0451.mcstructs.nbt.NbtTag;
import net.lenni0451.mcstructs.nbt.tags.*;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A provider to search for tag names and content.
 */
public class SearchProvider {

    private final ImNbtDrawer drawer;
    private final List<String> searchPaths = new ArrayList<>();
    private final Set<String> searchPathsSet = new LinkedHashSet<>();
    private final Set<String> expandPaths = new LinkedHashSet<>();
    private String search = "";
    private NbtTag lastTag = null;
    private int currentScrollIndex = -1;
    private String currentScrollPath = null;
    private boolean doScroll = false;

    public SearchProvider(final ImNbtDrawer drawer) {
        this.drawer = drawer;
    }

    /**
     * @return The amount of matches
     */
    public int getPathCount() {
        return this.searchPaths.size();
    }

    /**
     * @return The current scrolled index
     */
    public int getCurrentScrollIndex() {
        return this.currentScrollIndex;
    }

    /**
     * @return The current search string
     */
    public String getSearch() {
        return this.search;
    }

    /**
     * Set the current search string.<br>
     * This does not refresh the search, use {@link #refreshSearch()} for that.
     *
     * @param search The search string
     */
    public void setSearch(final String search) {
        this.search = search;
    }

    /**
     * Refresh the search with the last used tag.
     */
    public void refreshSearch() {
        this.buildSearchPaths(this.lastTag);
    }

    /**
     * Refresh the search with the given tag.
     *
     * @param tag The tag to search in
     */
    public void buildSearchPaths(@Nullable final NbtTag tag) {
        this.lastTag = tag;

        this.searchPaths.clear();
        this.searchPathsSet.clear();
        this.expandPaths.clear();
        this.currentScrollIndex = -1;
        this.currentScrollPath = null;
        this.doScroll = false;
        if (this.search.isEmpty() || tag == null) return;

        Map<String, NbtTag> tags = new LinkedHashMap<>();
        NbtPath.getTags(tags, tag, "");
        for (Map.Entry<String, NbtTag> entry : tags.entrySet()) {
            NbtPath.IPathNode[] paths = NbtPath.parse(entry.getKey());
            String name = paths[paths.length - 1].name();
            final Runnable addTag = () -> {
                this.searchPaths.add(entry.getKey());
                this.searchPathsSet.add(entry.getKey());
                this.expandParents(paths);
            };

            if (name.toLowerCase().contains(this.search.toLowerCase())) {
                addTag.run();
            } else {
                switch (entry.getValue().getNbtType()) {
                    case BYTE -> {
                        ByteTag byteTag = entry.getValue().asByteTag();
                        Byte value = NumberUtils.asByte(this.search);
                        if (value != null && byteTag.getValue() == value) addTag.run();
                    }
                    case SHORT -> {
                        ShortTag shortTag = entry.getValue().asShortTag();
                        Short value = NumberUtils.asShort(this.search);
                        if (value != null && shortTag.getValue() == value) addTag.run();
                    }
                    case INT -> {
                        IntTag intTag = entry.getValue().asIntTag();
                        Integer value = NumberUtils.asInt(this.search);
                        if (value != null && intTag.getValue() == value) addTag.run();
                    }
                    case LONG -> {
                        LongTag longTag = entry.getValue().asLongTag();
                        Long value = NumberUtils.asLong(this.search);
                        if (value != null && longTag.getValue() == value) addTag.run();
                    }
                    case FLOAT -> {
                        FloatTag floatTag = entry.getValue().asFloatTag();
                        Float value = NumberUtils.asFloat(this.search);
                        if (value != null && floatTag.getValue() == value) addTag.run();
                    }
                    case DOUBLE -> {
                        DoubleTag doubleTag = entry.getValue().asDoubleTag();
                        Double value = NumberUtils.asDouble(this.search);
                        if (value != null && doubleTag.getValue() == value) addTag.run();
                    }
                    case BYTE_ARRAY -> {
                        ByteArrayTag byteArrayTag = entry.getValue().asByteArrayTag();
                        Byte value = NumberUtils.asByte(this.search);
                        if (value != null) {
                            for (byte b : byteArrayTag.getValue()) {
                                if (b == value) {
                                    addTag.run();
                                    break;
                                }
                            }
                        }
                    }
                    case STRING -> {
                        StringTag stringTag = entry.getValue().asStringTag();
                        if (stringTag.getValue().toLowerCase().contains(this.search.toLowerCase())) addTag.run();
                    }
                    case INT_ARRAY -> {
                        IntArrayTag intArrayTag = entry.getValue().asIntArrayTag();
                        Integer value = NumberUtils.asInt(this.search);
                        if (value != null) {
                            for (int i : intArrayTag.getValue()) {
                                if (i == value) {
                                    addTag.run();
                                    break;
                                }
                            }
                        }
                    }
                    case LONG_ARRAY -> {
                        LongArrayTag longArrayTag = entry.getValue().asLongArrayTag();
                        Long value = NumberUtils.asLong(this.search);
                        if (value != null) {
                            for (long l : longArrayTag.getValue()) {
                                if (l == value) {
                                    addTag.run();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Build all paths of parent tags which should be expanded.
     *
     * @param paths The path nodes of the tag
     */
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

    /**
     * Check if the given path is searched.
     *
     * @param path The path to check
     * @return If the path is searched
     */
    public boolean isSearched(final String path) {
        if (this.search.isEmpty() || this.searchPaths.isEmpty()) return false;
        return this.searchPathsSet.contains(path);
    }

    /**
     * Check if the given path is targeted.<br>
     * The targeted path is the path which should be scrolled to.
     *
     * @param path The path to check
     * @return If the path is targeted
     */
    public boolean isTargeted(final String path) {
        if (this.search.isEmpty() || this.searchPaths.isEmpty() || this.currentScrollPath == null) return false;
        return this.currentScrollPath.equals(path);
    }

    /**
     * Check if the given path is expanded.
     *
     * @param path The path to check
     * @return If the path is expanded
     */
    public boolean isExpanded(final String path) {
        if (this.search.isEmpty() || this.searchPaths.isEmpty()) return false;
        return this.expandPaths.contains(path);
    }

    /**
     * Set the scroll position to the next or previous searched path.
     *
     * @param direction The direction to scroll
     */
    public void setDoScroll(final SearchDirection direction) {
        if (this.search.isEmpty() || this.searchPaths.isEmpty()) return;
        this.doScroll = true;
        this.currentScrollIndex += switch (direction) {
            case PREVIOUS -> -1;
            case NEXT -> 1;
        };
        this.currentScrollIndex %= this.searchPaths.size();
        if (this.currentScrollIndex < 0) this.currentScrollIndex = this.searchPaths.size() - 1;
        this.currentScrollPath = this.searchPaths.get(this.currentScrollIndex);
    }

    /**
     * Check if the given path should be scrolled to.
     *
     * @param path The path to check
     * @return If the path should be scrolled to
     */
    public boolean shouldDoScroll(final String path) {
        if (this.search.isEmpty() || this.searchPaths.isEmpty()) return false;
        if (this.doScroll && this.currentScrollPath.equals(path)) {
            this.doScroll = false;
            return true;
        }
        return false;
    }

    /**
     * Get the page which is required to show the scroll path.
     *
     * @param path The path to check
     * @return The page
     */
    public int getOpenedPage(final String path) {
        if (this.search.isEmpty() || this.searchPaths.isEmpty() || this.currentScrollPath == null) return -1;
        if (this.currentScrollPath.startsWith(path)) {
            String subPath = this.currentScrollPath.substring(path.length());
            if (!subPath.startsWith("[")) return -1;
            int index = Integer.parseInt(subPath.substring(1, subPath.indexOf(']')));
            return index / this.drawer.getLinesPerPage() + 1;
        }
        return -1;
    }


    public enum SearchDirection {
        PREVIOUS, NEXT
    }

}
